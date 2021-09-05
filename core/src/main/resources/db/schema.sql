drop table if exists phone2color;
drop table if exists colors;
drop table if exists stocks;
drop table if exists phones;
drop table if exists orders;
drop table if exists phone2order;

create table colors (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50),
  UNIQUE (code)
);

create table phones (
  id BIGINT AUTO_INCREMENT primary key,
  brand VARCHAR(50) NOT NULL,
  model VARCHAR(254) NOT NULL,
  price DECIMAL(10,2),
  displaySizeInches FLOAT,
  weightGr SMALLINT,
  lengthMm FLOAT,
  widthMm FLOAT,
  heightMm FLOAT,
  announced DATETIME,
  deviceType VARCHAR(50),
  os VARCHAR(100),
  displayResolution VARCHAR(50),
  pixelDensity SMALLINT,
  displayTechnology VARCHAR(50),
  backCameraMegapixels FLOAT,
  frontCameraMegapixels FLOAT,
  ramGb FLOAT,
  internalStorageGb FLOAT,
  batteryCapacityMah SMALLINT,
  talkTimeHours FLOAT,
  standByTimeHours FLOAT,
  bluetooth VARCHAR(50),
  positioning VARCHAR(100),
  imageUrl VARCHAR(254),
  description VARCHAR(4096),
  CONSTRAINT UC_phone UNIQUE (brand, model)
);

create table phone2color (
  phoneId BIGINT,
  colorId BIGINT,
  CONSTRAINT FK_phone2color_phoneId FOREIGN KEY (phoneId) REFERENCES phones (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_phone2color_colorId FOREIGN KEY (colorId) REFERENCES colors (id) ON DELETE CASCADE ON UPDATE CASCADE
);

create table stocks (
  phoneId BIGINT NOT NULL,
  stock SMALLINT NOT NULL CHECK (stock >= 0),
  reserved SMALLINT NOT NULL CHECK (reserved >= 0),
  UNIQUE (phoneId),
  CONSTRAINT FK_stocks_phoneId FOREIGN KEY (phoneId) REFERENCES phones (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT CHK_reserved_less_than_or_equals_stock CHECK (reserved <= stock)
);

create table orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  subtotal DECIMAL(10,2) NOT NULL,
  deliveryPrice DECIMAL(5,2) NOT NULL,
  totalPrice DECIMAL(10,2) NOT NULL,
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  deliveryAddress VARCHAR(100) NOT NULL,
  contactPhoneNo VARCHAR(50) NOT NULL,
  additionalInfo VARCHAR(200) NOT NULL,
  date TIMESTAMP,
  status ENUM('NEW', 'DELIVERED', 'REJECTED') NOT NULL
);

create table phone2order (
  phoneId BIGINT,
  orderId BIGINT,
  quantity INT NOT NULL,
  purchaseTimePrice DECIMAL(10,2) NOT NULL,
  CONSTRAINT FK_phone2order_phoneId FOREIGN KEY (phoneId) REFERENCES phones(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_phone2order_orderId FOREIGN KEY (orderId) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE
);