DROP TABLE IF EXISTS chat_member CASCADE;

CREATE TABLE chat_member (
    chat_id integer NOT NULL,
    member text NOT NULL
);

ALTER TABLE chat_member OWNER TO postgres;

DROP TABLE IF EXISTS chatroom CASCADE;
CREATE TABLE chatroom (
    id integer NOT NULL,
    location point NOT NULL,
    creator text NOT NULL,
    date timestamp with time zone DEFAULT now()
);

ALTER TABLE chatroom OWNER TO postgres;

DROP TABLE IF EXISTS message CASCADE;
CREATE TABLE message (
    id integer NOT NULL,
    date timestamp with time zone NOT NULL,
    content text NOT NULL,
    chat_id integer NOT NULL,
    poster text NOT NULL
);

ALTER TABLE message OWNER TO postgres;

DROP TABLE IF EXISTS user_weeat CASCADE;
CREATE TABLE user_weeat (
    username text NOT NULL
);

ALTER TABLE user_weeat OWNER TO postgres;


ALTER TABLE ONLY chat_member
    ADD CONSTRAINT chat_member_pkey PRIMARY KEY (chat_id, member);
ALTER TABLE ONLY chatroom
    ADD CONSTRAINT chatroom_pkey PRIMARY KEY (id);
ALTER TABLE ONLY message
    ADD CONSTRAINT message_pkey PRIMARY KEY (id);
ALTER TABLE ONLY user_weeat
    ADD CONSTRAINT user_weeat_pkey PRIMARY KEY (username);
ALTER TABLE ONLY chat_member
    ADD CONSTRAINT chat_member_chat_id_fkey FOREIGN KEY (chat_id) REFERENCES chatroom(id) ON DELETE CASCADE;
ALTER TABLE ONLY chat_member
    ADD CONSTRAINT chat_member_member_fkey FOREIGN KEY (member) REFERENCES user_weeat(username) ON DELETE CASCADE;
ALTER TABLE ONLY chatroom
    ADD CONSTRAINT chatroom_creator_fkey FOREIGN KEY (creator) REFERENCES user_weeat(username) ON DELETE CASCADE;
ALTER TABLE ONLY message
    ADD CONSTRAINT message_chat_id_fkey FOREIGN KEY (chat_id) REFERENCES chatroom(id) ON DELETE CASCADE;
ALTER TABLE ONLY message
    ADD CONSTRAINT message_poster_fkey FOREIGN KEY (poster) REFERENCES user_weeat(username) ON DELETE CASCADE;