package com.ahmedmatem.android.chesstar.config;

public class Constants {
    public static final String APP_PACKAGE = "com.ahmedmatem.android.chesstar";

    // HTTP Urls
    public static final String API_BASE_URL =
            "https://us-central1-chesstar-ccaa4.cloudfunctions.net/";

    // extras
    public static final String OPPONENT_EXTRA = "Opponent";
    public static final String SOURCE_EXTRA = "Source";
    public static final String DESTINATION_EXTRA = "Destination";

    // actions
    public static final String PLAYER_ACTION = APP_PACKAGE + ".PLAYER_ACTION";
    public static final String MOVE_ACTION = APP_PACKAGE + ".MOVE_ACTION";

    // http response contents
    public static final String RESPONSE_CONTENT_PLAYER = "player";
    public static final String RESPONSE_CONTENT_MOVE = "move";
}
