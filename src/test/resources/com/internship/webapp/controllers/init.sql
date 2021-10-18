
CREATE TABLE locations
(
    location_id    NUMBER(4)  NOT NULL
      CONSTRAINT loc_id_pk PRIMARY KEY
    , city           VARCHAR2(30)
);

CREATE TABLE departments
(
    department_id    NUMBER(4)
      CONSTRAINT dept_id_pk
          PRIMARY KEY
    , department_name  VARCHAR2(30)
      CONSTRAINT dept_name_nn  NOT NULL
    , manager_id       NUMBER(6)
    , location_id      NUMBER(4)
    ,                  CONSTRAINT dept_loc_fk
      FOREIGN KEY (location_id)
          REFERENCES locations (location_id)
);


CREATE TABLE employees
(
    employee_id    NUMBER(6)
      CONSTRAINT emp_emp_id_pk
          PRIMARY KEY
    , first_name     VARCHAR2(20)
    , last_name      VARCHAR2(25)
      CONSTRAINT emp_last_name_nn  NOT NULL
    , email          VARCHAR2(25)
      CONSTRAINT emp_email_nn  NOT NULL
    , CONSTRAINT     emp_email_uk
      UNIQUE (email)
    , phone_number   VARCHAR2(20)
    , hire_date      DATE
      CONSTRAINT emp_hire_date_nn  NOT NULL
    , job_id         VARCHAR2(10)
      CONSTRAINT emp_job_nn  NOT NULL
    , salary         NUMBER(8,2)
      CONSTRAINT emp_salary_min
          CHECK (salary > 0)
    , commission_pct NUMBER(2,2)
    , manager_id     NUMBER(6)
    , department_id  NUMBER(4)
);

CREATE SEQUENCE employees_seq
    START WITH     3
    INCREMENT BY   1
    NOCACHE
    NOCYCLE;

CREATE SEQUENCE departments_seq
    START WITH     30
    INCREMENT BY   10
    NOCACHE
    NOCYCLE;

INSERT INTO EMPLOYEES
VALUES (0, 'Alexey', 'Neikulov', 'alex.neikulov@gmail.com', '060748612', TO_DATE('17-10-2021', 'dd-MM-yyyy'), 'IT_PROG', 17000, 0, 100, 10);
INSERT INTO EMPLOYEES
VALUES (1, 'John', 'Wick', 'dog.wick@gmail.com', '060748481', TO_DATE('16-10-2021', 'dd-MM-yyyy'), 'IT_PROG', 15000, 0, 100, 10);

INSERT INTO LOCATIONS VALUES (10, 'Seattle');
INSERT INTO LOCATIONS VALUES (15, 'London');

INSERT INTO DEPARTMENTS VALUES (10, 'IT', 0, 10);
INSERT INTO DEPARTMENTS VALUES (20, 'Security', 1, 15);