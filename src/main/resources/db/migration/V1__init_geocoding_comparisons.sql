CREATE TABLE geocoding_comparisons
(
    id               BIGINT         NOT NULL AUTO_INCREMENT COMMENT 'Внутренний идентификатор записи',
    public_id        CHAR(36)       NOT NULL COMMENT 'Публичный UUID записи',
    address          VARCHAR(500)   NOT NULL COMMENT 'Исходный адрес',

    yandex_lat       DECIMAL(9, 6)  NULL COMMENT 'Широта от Яндекс',
    yandex_lon       DECIMAL(9, 6)  NULL COMMENT 'Долгота от Яндекс',
    yandex_precision VARCHAR(16)    NULL COMMENT 'Точность геокодирования Яндекса',

    dadata_lat       DECIMAL(9, 6)  NULL COMMENT 'Широт от DaData',
    dadata_lon       DECIMAL(9, 6)  NULL COMMENT 'Долгот от DaData',
    dadata_precision VARCHAR(16)    NULL COMMENT 'Точность геокодирования DaData',

    distance_meters  DECIMAL(10, 2) NULL COMMENT 'Расстояние между координатами Яндекса и DaData в метрах',
    created_at       TIMESTAMP(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        COMMENT 'Дата и время создания записи',

    CONSTRAINT pk_geocoding_comparisons
        PRIMARY KEY (id),
    CONSTRAINT uk_geocoding_comparisons_public_id
        UNIQUE (public_id)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = 'История сравнений результатов геокодирования'