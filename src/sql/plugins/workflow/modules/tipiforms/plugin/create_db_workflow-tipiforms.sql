DROP TABLE IF EXISTS workflow_task_tipiformsprovider_cf;

--
-- Table structure for table workflow_task_tipiformsprovider_cf
--
CREATE TABLE workflow_task_tipiformsprovider_cf(
id_task INT DEFAULT 0 NOT NULL,
id_form INT DEFAULT 0 NOT NULL,
id_question_refdet INT DEFAULT 0 NOT NULL,
id_question_amount INT DEFAULT 0 NOT NULL,
id_question_email INT DEFAULT 0 NOT NULL,
PRIMARY KEY (id_task)
);