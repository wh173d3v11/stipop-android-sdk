package io.stipop.models

enum class StipopApiEnum {
    INIT_SDK,
    GET_STICKER_PACKAGE,
    GET_RECOMMENDED_KEYWORDS,
    GET_HOME_SOURCES,   // GET_CURATION_PACKAGES && GET_RECOMMENDED_KEYWORDS
    GET_RECENTLY_SENT_STICKERS,
    GET_FAVORITE_STICKERS,
    GET_MY_STICKERS,
    GET_MY_HIDDEN_STICKERS,
    PUT_MY_STICKER_FAVORITE,
    PUT_MY_STICKERS_ORDERS,
    PUT_MY_STICKER_VISIBILITY,
    GET_TRENDING_STICKER_PACKAGES,
    GET_STICKERS,
    GET_NEW_STICKER_PACKAGES,
    POST_DOWNLOAD_STICKERS,
    TRACK_CONFIG,
    TRACK_VIEW_PICKER,
    TRACK_VIEW_SEARCH,
    TRACK_VIEW_STORE,
    TRACK_VIEW_NEW,
    TRACK_VIEW_MY_STICKER,
    TRACK_VIEW_PACKAGE,
    TRACK_USING_STICKER,
    TRACK_ERROR
}