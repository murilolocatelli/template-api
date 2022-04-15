CREATE TABLE public.user_template (
	id bigserial NOT NULL,
	active bool NOT NULL,
	age int8 NULL,
	"date" date NULL,
	date_time timestamp NULL,
	height float8 NULL,
	"name" varchar(255) NOT NULL,
	CONSTRAINT user_template_pkey PRIMARY KEY (id)
);
