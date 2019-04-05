TRUNCATE TABLE MOBILE_SUBSCRIBER;
TRUNCATE TABLE COMPANY;
TRUNCATE TABLE PERSON;
DELETE FROM CUSTOMER;

INSERT INTO CUSTOMER (ID, ADDRESS) VALUES (1, 'Long street 1, 00-111 Nice City');
INSERT INTO CUSTOMER (ID, ADDRESS) VALUES (2, 'Long street 2, 00-112 Clean City');
INSERT INTO CUSTOMER (ID, ADDRESS) VALUES (3, 'Long street 3, 00-113 Sunny City');
INSERT INTO CUSTOMER (ID, ADDRESS) VALUES (4, 'Long street 4, 00-114 Fun City');
INSERT INTO CUSTOMER (ID, ADDRESS) VALUES (5, 'Long street 5, 00-115 Small City');
INSERT INTO CUSTOMER (ID, ADDRESS) VALUES (6, 'Long street 6, 00-116 Big City');

INSERT INTO PERSON (ID, FIRST_NAME, LAST_NAME, DOCUMENT_ID) VALUES (1, 'Mike', 'Boring', 1234);
INSERT INTO PERSON (ID, FIRST_NAME, LAST_NAME, DOCUMENT_ID) VALUES (3, 'Don', 'Lazy', 2345);
INSERT INTO PERSON (ID, FIRST_NAME, LAST_NAME, DOCUMENT_ID) VALUES (4, 'Michael', 'Funny', 3456);

INSERT INTO COMPANY(ID, COMPANY_NAME, TAX_ID) VALUES (2, 'Big Company', 1231);
INSERT INTO COMPANY(ID, COMPANY_NAME, TAX_ID) VALUES (5, 'Small Company', 1234);
INSERT INTO COMPANY(ID, COMPANY_NAME, TAX_ID) VALUES (6, 'Family Company', 1256);

INSERT INTO MOBILE_SUBSCRIBER (ID, MSISDN, CUSTOMER_ID_OWNER, CUSTOMER_ID_USER, SERVICE_TYPE, SERVICE_START_DATE) VALUES (1, '48500123456', 1, 2, 'MOBILE_PREPAID', 1554308106460);
INSERT INTO MOBILE_SUBSCRIBER (ID, MSISDN, CUSTOMER_ID_OWNER, CUSTOMER_ID_USER, SERVICE_TYPE, SERVICE_START_DATE) VALUES (2, '48511123456', 1, 1, 'MOBILE_PREPAID', 1554318116460);
INSERT INTO MOBILE_SUBSCRIBER (ID, MSISDN, CUSTOMER_ID_OWNER, CUSTOMER_ID_USER, SERVICE_TYPE, SERVICE_START_DATE) VALUES (3, '48532432456', 1, 6, 'MOBILE_POSTPAID', 1554308106460);
INSERT INTO MOBILE_SUBSCRIBER (ID, MSISDN, CUSTOMER_ID_OWNER, CUSTOMER_ID_USER, SERVICE_TYPE, SERVICE_START_DATE) VALUES (4, '48549381237', 2, 5, 'MOBILE_POSTPAID', 1554308106460);
INSERT INTO MOBILE_SUBSCRIBER (ID, MSISDN, CUSTOMER_ID_OWNER, CUSTOMER_ID_USER, SERVICE_TYPE, SERVICE_START_DATE) VALUES (5, '48603324456', 5, 5, 'MOBILE_POSTPAID', 1554308106460);
INSERT INTO MOBILE_SUBSCRIBER (ID, MSISDN, CUSTOMER_ID_OWNER, CUSTOMER_ID_USER, SERVICE_TYPE, SERVICE_START_DATE) VALUES (6, '48789123453', 2, 5, 'MOBILE_POSTPAID', 1554308106462);
INSERT INTO MOBILE_SUBSCRIBER (ID, MSISDN, CUSTOMER_ID_OWNER, CUSTOMER_ID_USER, SERVICE_TYPE, SERVICE_START_DATE) VALUES (7, '48987432452', 3, 4, 'MOBILE_PREPAID', 1554308106465);
INSERT INTO MOBILE_SUBSCRIBER (ID, MSISDN, CUSTOMER_ID_OWNER, CUSTOMER_ID_USER, SERVICE_TYPE, SERVICE_START_DATE) VALUES (8, '48521349451', 6, 2, 'MOBILE_PREPAID', 1554308106490);

COMMIT;