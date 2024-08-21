/*
 * Copyright (C) 2024 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.visual;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TamableAnimalsHandler extends ChannelDuplexHandler
{
    //TODO optimize by caching
    private static final String BUNDLE_PACKET = "ClientboundBundlePacket";
    private static final String ENTITY_DATA_PACKET = "ClientboundSetEntityDataPacket";

    private static final ConcurrentHashMap<Class<?>, Method> subPacketsMethodCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Method> packedItemsMethodCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Field> valueFieldCache = new ConcurrentHashMap<>();


    /**
     * Handles a ClientboundBundlePacket packet by iterating over its sub-packets.
     * <p>
     * If it is a sub-packet of type ClientboundSetEntityDataPacket, it will deal with the sub-packet.
     * by using the {@link #processEntityData(Object)} method.
     *
     * @param msg the packet send by the minecraft network
     * @throws IllegalAccessException when the method is not accessible
     * @throws NoSuchMethodException when the method was removed
     * @throws InvocationTargetException when the method cannot be invoked.
     */
    private static void processBundle(@NotNull Object msg) throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException
    {
        Method subPacketsMethod = subPacketsMethodCache.computeIfAbsent(msg.getClass(), clazz ->
        {
            try
            {
                return clazz.getMethod("subPackets");
            }
            catch (NoSuchMethodException e)
            {
                throw new RuntimeException(e);
            }
        });

        Iterable<?> subPackets = (Iterable<?>) subPacketsMethod.invoke(msg);
        for (Object subPacket : subPackets)
        {
            if (Objects.equals(subPacket.getClass().getSimpleName(), ENTITY_DATA_PACKET))
            {
                processEntityData(subPacket);
                break;
            }
        }
    }

    /**
     * Processes ClientboundSetEntityDataPacket by removing the owner attribute
     * <p>
     * This method processes the ClientboundSetEntityDataPacket by iterating over the packedItems including the owner
     * attribute
     * and removes it in order to make sure a tamed animal is not added to the same team as the user.
     *
     * @param msg the packet send by the minecraft network
     * @throws IllegalAccessException when the method is not accessible
     * @throws NoSuchMethodException when the method was removed
     * @throws InvocationTargetException when the method cannot be invoked.
     */
    private static void processEntityData(@NotNull Object msg) throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException
    {
        Method packedItemsMethod = packedItemsMethodCache.computeIfAbsent(msg.getClass(), clazz ->
        {
            try
            {
                return clazz.getDeclaredMethod("packedItems");
            }
            catch (NoSuchMethodException e)
            {
                throw new RuntimeException(e);
            }
        });

        List<?> packedItems = (List<?>) packedItemsMethod.invoke(msg);
        packedItems.removeIf(dataValue ->
        {
            try
            {
                return isOwnerAttribute(dataValue);
            }
            catch (NoSuchFieldException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Checks if the given value is the owner id.
     * <p>
     * This method checks whether the given data is the owner id.
     *
     * @param dataValue the data passed.
     * @return whether it is the owner attribute
     * @throws NoSuchFieldException when the value field does not exist.
     * @throws IllegalAccessException when the field is not accessible
     */
    private static boolean isOwnerAttribute(@NotNull Object dataValue) throws NoSuchFieldException,
            IllegalAccessException
    {
        Class<?> dataValueClass = dataValue.getClass();
        Field valueField = valueFieldCache.computeIfAbsent(dataValueClass, cls ->
        {
            try
            {
                Field field = cls.getDeclaredField("value");
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException)
            {
                throw new RuntimeException(noSuchFieldException);
            }
        });

        Object value = valueField.get(dataValue);
        if (!(value instanceof Optional<?> optional))
        {
            return false;
        }

        return optional.isPresent() && optional.get() instanceof UUID;
    }

    @Override
    public void write(@NotNull ChannelHandlerContext ctx,
            @NotNull Object msg,
            @NotNull ChannelPromise promise) throws Exception
    {
        String simpleName = msg.getClass().getSimpleName();
        switch (simpleName)
        {
            case BUNDLE_PACKET -> processBundle(msg);
            case ENTITY_DATA_PACKET -> processEntityData(msg);
        }

        super.write(ctx, msg, promise);
    }
}
