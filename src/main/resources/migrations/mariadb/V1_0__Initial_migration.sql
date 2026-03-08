CREATE TABLE IF NOT EXISTS ksvi_visual_rank
(
    id VARCHAR(20) NOT NULL,
    prefix JSON NOT NULL,
    suffix JSON NULL DEFAULT NULL,
    color INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES ksvp_rank(id)
);

CREATE TABLE IF NOT EXISTS ksvi_visual_suffix(id VARCHAR(20) NOT NULL, player_id VARCHAR(36) NOT NULL, content JSON NOT NULL, PRIMARY KEY(id, player_id), FOREIGN KEY (player_id) REFERENCES ksvp_player(id));