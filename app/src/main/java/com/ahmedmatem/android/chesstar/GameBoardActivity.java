package com.ahmedmatem.android.chesstar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import com.ahmedmatem.android.chesstar.adapters.TileAdapter;
import com.ahmedmatem.chess.engine.board.Board;
import com.ahmedmatem.chess.engine.board.Tile;
import com.ahmedmatem.chess.engine.models.Player;
import com.ahmedmatem.chess.util.Alliance;

import java.util.Map;

import static com.ahmedmatem.android.chesstar.config.Constants.OPPONENT_EXTRA;

public class GameBoardActivity extends AppCompatActivity {
    private static final Map<Integer,Tile> tiles = Board.getTiles();

    private Preferences mPreference;

    private Player me;
    private Player opponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        mPreference = new Preferences(this);

        Intent intent = getIntent();
        if (intent.hasExtra(OPPONENT_EXTRA)) {
            com.ahmedmatem.android.chesstar.models.Player player =
                    intent.getParcelableExtra(OPPONENT_EXTRA);
            opponent = new Player(player.name, player.token, player.alliance);
            me = new Player(mPreference.getName(), mPreference.getToken(),
                    opponent.getAlliance() == Alliance.WHITE ? Alliance.BLACK : Alliance.WHITE);

            TextView opponentName = findViewById(R.id.opponent_name);
            opponentName.setText(opponent.getName());
            TextView myName = findViewById(R.id.me);
            myName.setText(me.getName());
        }

        GridView chessBoard = (GridView) findViewById(R.id.chess_board);
        chessBoard.setAdapter(new TileAdapter(this, tiles));
    }
}
