[![CircleCI](https://circleci.com/gh/hubson87/mobile-rest.svg?style=svg)](https://circleci.com/gh/hubson87/mobile-rest)

I've assumed that mobile number cannot exist without the subscriber data, that's why I've put everything into one table with unique value.
Otherwise I would just add @OneToOne mapping and put mobile numbers into a different table with cascade set.

Operations descriptions:
1. Return all mobile numbers from the database:
    GET mapping for /mobile/subscribers path with empty parameters
2. Return all mobile numbers for the database:
    GET mapping for /mobile/subscribers path with parameters that needs to be used
3. Add a mobile number to the database
    POST mapping for /mobile/subscribers path with filled object (without date)
4. Change mobile number from prepaid to postpaid and vice versa
    PUT or PATCH method (depends what customer wants to use) for /mobile/subscribers/ID with provided changed service plan (for post it needs to contain also other data)
5. Delete a mobile number from the database
    DELETE method for path /mobile/subscribers/ID
6. Assign different owners/users
    Same as in point '4', just with new user/owner id provided
    
I've used H2 as dependencies, so spring boot can easily provide the database config automatic way.
For production purposes, I'd need to have the configuration provided, then I could configure the access.
I wanted also to use some CommandLineRunner to provide the data for tests, but if has to be set as production quality, I have removed that.

Swagger UI is active, so you can do the tests easily.
I've also provided the documentation in main folder (Swagger3_documentation.yaml file)
