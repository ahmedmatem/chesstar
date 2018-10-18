package com.ahmedmatem.android.chesstar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ahmedmatem.android.chesstar.adapters.TileAdapter;
import com.ahmedmatem.android.chesstar.config.Constants;
import com.ahmedmatem.chess.engine.board.Board;
import com.ahmedmatem.chess.engine.board.Tile;
import com.ahmedmatem.chess.engine.models.Player;
import com.ahmedmatem.chess.engine.pieces.Piece;
import com.ahmedmatem.chess.util.Alliance;
import com.ahmedmatem.chess.util.Position;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ahmedmatem.android.chesstar.config.Constants.DESTINATION_EXTRA;
import static com.ahmedmatem.android.chesstar.config.Constants.RESPONSE_CONTENT_MOVE;
import static com.ahmedmatem.android.chesstar.config.Constants.OPPONENT_EXTRA;
import static com.ahmedmatem.android.chesstar.config.Constants.SOURCE_EXTRA;

public class GameBoardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final Board board = new Board();

    private BroadcastReceiver mMoveMessageReceiver;

    private FirebaseFunctions mFunctions;

    private Preferences mPreference;

    private Player player1;
    private Player player2;

    private Tile sourceTile;
    private List<Tile> destinationTiles;
    private int sourcePosition;
    private int destinationPosition;
    private static boolean whiteAllianceBoardView;

    private GridView chessBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        mFunctions = FirebaseFunctions.getInstance();

        mPreference = new Preferences(this);

        Intent intent = getIntent();
        if (intent.hasExtra(OPPONENT_EXTRA)) {
            com.ahmedmatem.android.chesstar.models.Player opponent =
                    intent.getParcelableExtra(OPPONENT_EXTRA);

            player2 = new Player(opponent.name, opponent.token, opponent.alliance);
            Alliance player1Alliance =
                    player2.getAlliance() == Alliance.WHITE ? Alliance.BLACK : Alliance.WHITE;
            player1 = new Player(mPreference.getName(), mPreference.getToken(), player1Alliance);

            whiteAllianceBoardView = player1.getAlliance() == Alliance.WHITE;

            TextView player2Name = findViewById(R.id.player2);
            player2Name.setText(player2.getName() + (!whiteAllianceBoardView ? "-white" : "-black"));
            TextView player1Name = findViewById(R.id.player1);
            player1Name.setText(player1.getName() + (whiteAllianceBoardView ? "-white" : "-black"));
        }

        chessBoard = findViewById(R.id.chess_board);
        if(whiteAllianceBoardView) {
            chessBoard.setAdapter(new TileAdapter(this, Board.getTiles()));
            chessBoard.setOnItemClickListener(this);
        } else {
            chessBoard.setAdapter(new TileAdapter(this, Board.reverseTiles()));
        }

        mMoveMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(SOURCE_EXTRA) && intent.hasExtra(DESTINATION_EXTRA)) {
                    int source = intent.getIntExtra(SOURCE_EXTRA, -1);
                    int destination = intent.getIntExtra(DESTINATION_EXTRA, -1);
                    Tile sourceTile = Board.getTiles().get(source);
                    sourceTile.getPiece().move(source, destination);
                    updateBoardOnMove();
                    chessBoard.setOnItemClickListener(GameBoardActivity.this);
                }
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!whiteAllianceBoardView) {
            position = Position.reverce(position);
        }
        Tile clickedTile = Board.getTiles().get(position);
        Piece clickedPiece = clickedTile.getPiece();

        if ( whiteAllianceBoardView &&
                clickedTile.isOccupied() &&
                clickedPiece.getAlliance() == Alliance.WHITE ) {
            sourceTile = clickedTile;
            destinationTiles =
                    clickedPiece.calculateDestinationTiles(position, Board.getTiles());
            updateBoardOnPieceSelected(sourceTile, destinationTiles);
        } else if (!whiteAllianceBoardView &&
                clickedTile.isOccupied() &&
                clickedPiece.getAlliance() == Alliance.BLACK) {
            sourceTile = clickedTile;
            destinationTiles = clickedPiece
                    .calculateDestinationTiles(position, Board.getTiles());
            updateBoardOnPieceSelected(sourceTile, destinationTiles);
        } else if(destinationTiles != null && destinationTiles.contains(clickedTile)){
            destinationTiles = null;
            sourcePosition = sourceTile.getPosition();
            destinationPosition = position;
            sourceTile.getPiece().move(sourcePosition, destinationPosition);
            sendMoveToOpponent();
            updateBoardOnMove();
            // disable click action after move
            chessBoard.setOnItemClickListener(null);
        }
    }

    private void updateBoardOnMove() {
        clearPreviousUpdate();
        // update chess board
        if(whiteAllianceBoardView) {
            chessBoard.setAdapter(new TileAdapter(this, Board.getTiles()));
        } else {
            chessBoard.setAdapter(new TileAdapter(this, Board.reverseTiles()));
        }
    }

    private void sendMoveToOpponent() {
        Map<String,String> data = new HashMap<>();
        data.put("responseContent", RESPONSE_CONTENT_MOVE);
        String opponentToken;
        if (whiteAllianceBoardView) {
            opponentToken = player1.getAlliance() == Alliance.WHITE ?
                    player2.getMessagingToken() : player1.getMessagingToken();
        } else {
            opponentToken = player1.getAlliance() == Alliance.BLACK ?
                    player2.getMessagingToken() : player1.getMessagingToken();
        }
        data.put("token", opponentToken);
        data.put("source", String.valueOf(sourcePosition));
        data.put("destination", String.valueOf(destinationPosition));
        mFunctions.getHttpsCallable("onMove").call(data);
    }

    private void updateBoardOnPieceSelected(Tile sourceTile, List<Tile> destinationTiles) {
        clearPreviousUpdate();

        if(destinationTiles != null && destinationTiles.size() > 0) {
            sourceTile.isSelected = true;
            int destinationPosition;
            Tile destinationTile;
            for (Tile tile : destinationTiles) {
                destinationPosition = tile.getPosition();
                destinationTile = Board.getTiles().get(destinationPosition);
                if(!destinationTile.isOccupied()){
                    destinationTile.isPossibleDestination = true;
                }
            }
            // update chess board
            if(whiteAllianceBoardView) {
                ((TileAdapter) chessBoard.getAdapter()).setTiles(Board.getTiles());
            } else {
                ((TileAdapter) chessBoard.getAdapter()).setTiles(Board.reverseTiles());
            }
        }
    }

    private void clearPreviousUpdate() {
        Tile tile;
        for (int i = 0; i < 64; i++) {
            tile = Board.getTiles().get(i);
            if(tile.isSelected){
                tile.isSelected = false;
            }
            if (tile.isPossibleDestination) {
                tile.isPossibleDestination = false;
            }
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMoveMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMoveMessageReceiver, new IntentFilter(Constants.MOVE_ACTION));
        super.onResume();
    }
}
