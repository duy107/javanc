drop database if exists test;
create database test;
use test;


CREATE TABLE address (
    city_id BIGINT,
    district_id BIGINT,
    id BIGINT NOT NULL AUTO_INCREMENT,
    ward_id BIGINT,
    detail VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

--
CREATE TABLE category (
    deleted TINYINT(1),
    id BIGINT NOT NULL AUTO_INCREMENT,
    parent_id BIGINT,
    name VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;
--
CREATE TABLE color (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;
--
CREATE TABLE detail (
    color_id BIGINT,
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT,
    size_id BIGINT,
    sold_count BIGINT,
    stock BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE discount (
    deleted TINYINT(1),
    percent FLOAT(53),
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE feedback (
    deleted TINYINT(1),
    rating FLOAT(23),
    feedback_time DATETIME(6),
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT,
    user_id BIGINT,
    description VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE image (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT,
    src VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE invalidated_token (
    expiry_time DATETIME(6),
    id VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE order_product (
    price FLOAT(23),
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE orders (
    deleted TINYINT(1),
    address_id BIGINT NOT NULL,
    id BIGINT NOT NULL AUTO_INCREMENT,
    payment_id BIGINT,
    time DATETIME(6),
    user_id BIGINT,
    status VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE payment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT,
    type VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE permission (
    deleted TINYINT(1),
    id BIGINT NOT NULL AUTO_INCREMENT,
    description VARCHAR(255),
    name VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE product (
    deleted BIT,
    price FLOAT(23),
    category_id BIGINT,
    created_at DATETIME(6),
    id BIGINT NOT NULL AUTO_INCREMENT,
    quantity BIGINT,
    updated_at DATETIME(6),
    description VARCHAR(255),
    name VARCHAR(255),
    slug VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE product_discount (
    discount_id BIGINT,
    end_time DATETIME(6),
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT,
    start_time DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE product_shoppingcart (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT,
    quantity BIGINT,
    shoppingcart_id BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE role (
    deleted TINYINT(1),
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(255),
    name VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE role_permissions (
    permission_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE shoppingcart (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE size (
    id BIGINT NOT NULL AUTO_INCREMENT,
    description VARCHAR(255),
    name VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user (
    deleted TINYINT(1),
    status BIT,
    created_at DATETIME(6),
    id BIGINT NOT NULL AUTO_INCREMENT,
    updated_at DATETIME(6),
    avatar VARCHAR(512),
    email VARCHAR(255),
    name VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user_address (
    is_default BIT,
    address_id BIGINT NOT NULL,
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user_role (
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL
) ENGINE=InnoDB;

--     

ALTER TABLE detail
    ADD CONSTRAINT fk_detail_color_id FOREIGN KEY (color_id) REFERENCES color(id),
    ADD CONSTRAINT fk_detail_product_id FOREIGN KEY (product_id) REFERENCES product(id),
    ADD CONSTRAINT fk_detail_size_id FOREIGN KEY (size_id) REFERENCES size(id);
    
ALTER TABLE feedback
    ADD CONSTRAINT fk_feedback_product_id FOREIGN KEY (product_id) REFERENCES product(id),
    ADD CONSTRAINT fk_feedback_user_id FOREIGN KEY (user_id) REFERENCES user(id);
ALTER TABLE image
    ADD CONSTRAINT fk_image_product_id FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE order_product
    ADD CONSTRAINT fk_order_product_order_id FOREIGN KEY (order_id) REFERENCES orders(id),
    ADD CONSTRAINT fk_order_product_product_id FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_address_id FOREIGN KEY (address_id) REFERENCES address(id),
    ADD CONSTRAINT fk_orders_payment_id FOREIGN KEY (payment_id) REFERENCES payment(id),
    ADD CONSTRAINT fk_orders_user_id FOREIGN KEY (user_id) REFERENCES user(id);
ALTER TABLE product_discount
    ADD CONSTRAINT fk_product_discount_discount_id FOREIGN KEY (discount_id) REFERENCES discount(id),
    ADD CONSTRAINT fk_product_discount_product_id FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_permission_id FOREIGN KEY (permission_id) REFERENCES permission(id),
    ADD CONSTRAINT fk_role_permissions_role_id FOREIGN KEY (role_id) REFERENCES role(id);
ALTER TABLE shoppingcart
    ADD CONSTRAINT fk_shoppingcart_user_id FOREIGN KEY (user_id) REFERENCES user(id);
ALTER TABLE user_address
    ADD CONSTRAINT fk_user_address_address_id FOREIGN KEY (address_id) REFERENCES address(id),
    ADD CONSTRAINT fk_user_address_user_id FOREIGN KEY (user_id) REFERENCES user(id);
ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role(id),
    ADD CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES user(id);
ALTER TABLE payment
    ADD CONSTRAINT fk_payment_product_id FOREIGN KEY (product_id) REFERENCES product(id);


-- insert

insert into role(deleted, name, code) values (1, "admin", "ADMIN"), (1, "user", "USER");

insert into permission (deleted, description, name) values
(0, 'Xóa sản phẩm', 'PRODUCT_DELETE'),
(0, 'Thêm sản phẩm', 'PRODUCT_ADD'),
(0, 'Sửa sản phẩm', 'PRODUCT_UPDATE'),
(0, 'Thêm quyền', 'ROLE_ADD'),
(0, 'Xóa quyền', 'ROLE_DELETE'),
(0, 'Phân quyền', 'ROLE_PERMISSION'),
(0, 'Xem quyền', 'ROLE_VIEW'),
(0, 'Sửa quyền', 'ROLE_UPDATE'),
(0, 'Xem sản phẩm', 'PRODUCT_VIEW');