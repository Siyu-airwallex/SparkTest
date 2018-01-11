\c validation

-- Table: public.bankdirectory

-- DROP TABLE public.bankdirectory;

CREATE TABLE public.bankdirectory
(
    modification_flag character varying(1) COLLATE pg_catalog."default" NOT NULL,
    record_key character varying(12) COLLATE pg_catalog."default" NOT NULL,
    office_type character varying(2) COLLATE pg_catalog."default" NOT NULL,
    parent_office_key character varying(12) COLLATE pg_catalog."default" NOT NULL,
    head_office_key character varying(12) COLLATE pg_catalog."default" NOT NULL,
    legal_type character varying(1) COLLATE pg_catalog."default" NOT NULL,
    legal_parent_key character varying(12) COLLATE pg_catalog."default" NOT NULL,
    group_type character varying(6) COLLATE pg_catalog."default",
    group_parent_key character varying(12) COLLATE pg_catalog."default",
    institution_status character varying(4) COLLATE pg_catalog."default",
    cooperative_group_key character varying(12) COLLATE pg_catalog."default",
    iso_lei_code character varying(20) COLLATE pg_catalog."default",
    bic8 character varying(8) COLLATE pg_catalog."default",
    branch_bic character varying(3) COLLATE pg_catalog."default",
    bic character varying(11) COLLATE pg_catalog."default",
    chips_uid character varying(6) COLLATE pg_catalog."default",
    national_id character varying(15) COLLATE pg_catalog."default",
    connected_bic character varying(11) COLLATE pg_catalog."default",
    institution_name character varying(105) COLLATE pg_catalog."default" NOT NULL,
    branch_information character varying(70) COLLATE pg_catalog."default",
    pob_number character varying(35) COLLATE pg_catalog."default",
    street_address_1 character varying(35) COLLATE pg_catalog."default",
    street_address_2 character varying(35) COLLATE pg_catalog."default",
    street_address_3 character varying(35) COLLATE pg_catalog."default",
    street_address_4 character varying(35) COLLATE pg_catalog."default",
    city character varying(35) COLLATE pg_catalog."default",
    cps character varying(90) COLLATE pg_catalog."default",
    zip_code character varying(15) COLLATE pg_catalog."default",
    country_name character varying(70) COLLATE pg_catalog."default" NOT NULL,
    iso_country_code character varying(2) COLLATE pg_catalog."default" NOT NULL,
    timezone character varying(1) COLLATE pg_catalog."default",
    subtype_indicator character varying(4) COLLATE pg_catalog."default",
    network_connectivity character varying(3) COLLATE pg_catalog."default",
    branch_qualifiers character varying(35) COLLATE pg_catalog."default",
    service_codes character varying(60) COLLATE pg_catalog."default",
    ssi_group_key character varying(12) COLLATE pg_catalog."default",
    iban_key character varying(12) COLLATE pg_catalog."default",
    field_a character varying(35) COLLATE pg_catalog."default",
    field_b character varying(70) COLLATE pg_catalog."default",
    CONSTRAINT bankdirectory_pkey PRIMARY KEY (record_key)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.bankdirectory
    OWNER to postgres;

-- Index: bic

-- DROP INDEX public.bic;

CREATE INDEX bic
    ON public.bankdirectory USING btree
    (bic COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: bic8

-- DROP INDEX public.bic8;

CREATE INDEX bic8
    ON public.bankdirectory USING btree
    (bic8 COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: country_code

-- DROP INDEX public.country_code;

CREATE INDEX country_code
    ON public.bankdirectory USING btree
    (iso_country_code COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: national_id

-- DROP INDEX public.national_id;

CREATE INDEX national_id
    ON public.bankdirectory USING btree
    (national_id COLLATE pg_catalog."default")
    TABLESPACE pg_default;


