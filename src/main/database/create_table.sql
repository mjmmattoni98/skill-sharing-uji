CREATE TYPE collaboration_state as ENUM ('active', 'inactive');
CREATE TYPE skill_level as ENUM ('expert', 'average', 'low');


CREATE TABLE student (
    username VARCHAR(8) NOT NULL,
    password TEXT NOT NULL,
    balance_hours INTEGER NOT NULL DEFAULT 0,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(40) NOT NULL,
    surname VARCHAR(40) NOT NULL,
    email VARCHAR(40) NOT NULL,
    street VARCHAR(40) NOT NULL,
    number INTEGER NOT NULL,
    pc INTEGER NOT NULL,
    locality VARCHAR(30) NOT NULL,
    is_skp BOOLEAN NOT NULL DEFAULT FALSE,
    degree TEXT NOT NULL,
    CONSTRAINT student_pk PRIMARY KEY (username),
    CONSTRAINT student_pc_ri CHECK (pc > 0),
    CONSTRAINT student_number_ri CHECK (number > 0)
);

CREATE TABLE skill (
    name VARCHAR(30) NOT NULL,
    description VARCHAR(30) NOT NULL,
    level SKILL_LEVEL NOT NULL,
    canceled BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT skill_pk PRIMARY KEY (name)
);

CREATE TABLE email(
    id SERIAL,
    send_date DATE NOT NULL,
    sender VARCHAR(40) NOT NULL,
    receiver VARCHAR(40) NOT NULL,
    subject VARCHAR(20) NOT NULL,
    body TEXT NOT NULL,
    CONSTRAINT email_pk PRIMARY KEY (id)
);

CREATE TABLE offer (
    id SERIAL,
    name VARCHAR(30) NOT NULL,
    username VARCHAR(8) NOT NULL,
    start_date DATE NOT NULL,
    finish_date DATE,
    description TEXT NOT NULL,
    canceled BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT offer_pk PRIMARY KEY (id),
    CONSTRAINT offer_date_check CHECK (start_date <= finish_date OR finish_date IS NULL),
    CONSTRAINT offer_student_fk FOREIGN KEY (username) REFERENCES student(username) ON DELETE  RESTRICT ON UPDATE CASCADE,
    CONSTRAINT offer_skill_fk FOREIGN KEY (name) REFERENCES skill(name) ON DELETE  RESTRICT ON UPDATE CASCADE
);

CREATE TABLE request (
    id SERIAL,
    name VARCHAR(30) NOT NULL,
    username VARCHAR(8) NOT NULL,
    start_date DATE NOT NULL,
    finish_date DATE,
    description TEXT NOT NULL,
    canceled BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT request_pk PRIMARY KEY (id),
    CONSTRAINT request_date_ri CHECK (start_date <= finish_date OR finish_date IS NULL),
    CONSTRAINT request_student_fk FOREIGN KEY (username) REFERENCES student(username) ON DELETE  RESTRICT ON UPDATE CASCADE,
    CONSTRAINT request_skill_fk FOREIGN KEY (name) REFERENCES skill(name) ON DELETE  RESTRICT ON UPDATE CASCADE
);

CREATE TABLE collaboration (
    id_offer INTEGER NOT NULL,
    id_request INTEGER NOT NULL,
    hours INTEGER NOT NULL DEFAULT 0,
    assessment INTEGER NOT NULL DEFAULT 1,
    state COLLABORATION_STATE NOT NULL DEFAULT 'active',
    CONSTRAINT collaboration_pk PRIMARY KEY (id_offer, id_request),
    CONSTRAINT collaboration_fk_offer FOREIGN KEY (id_offer) REFERENCES offer(id) ON DELETE  RESTRICT ON UPDATE CASCADE,
    CONSTRAINT collaboration_fk_request FOREIGN KEY (id_request) REFERENCES request(id) ON DELETE  RESTRICT ON UPDATE CASCADE,
    CONSTRAINT collaboration_hours_ri CHECK (hours >= 0),
    CONSTRAINT collaboration_assessment_ri CHECK (assessment > 0 AND assessment <= 5)
);

CREATE TABLE message (
    id_offer INTEGER NOT NULL,
    id_request INTEGER NOT NULL,
    date_time TIMESTAMP NOT NULL,
    text TEXT NOT NULL,
    CONSTRAINT message_pk PRIMARY KEY (id_offer, id_request, date_time),
    CONSTRAINT message_fk FOREIGN KEY (id_offer, id_request) REFERENCES collaboration(id_offer, id_request) ON DELETE  RESTRICT ON UPDATE CASCADE
);
