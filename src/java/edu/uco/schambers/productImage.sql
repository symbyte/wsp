/* database: WSP (username: app, password: app)
 * jdbc resource name: jdbc/fildDB referenced in Bean
*/

create table productImage(
    prodid integer primary key,
    FILE_NAME VARCHAR(255),
    FILE_TYPE VARCHAR(255),
    FILE_SIZE BIGINT,
    FILE_CONTENTS BLOB  /* binary data */
);