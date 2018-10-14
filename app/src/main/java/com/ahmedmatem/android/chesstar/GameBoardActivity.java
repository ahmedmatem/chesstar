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

public class GameBoardActivity extends AppCompatActivity {

    private static final Board board = new Board();
    private static Map<Integer,Tile> tiles = board.getTiles();
//    private static Map<Integer,Tile> reverseTiles;

    private BroadcastReceiver mMoveMessageReceiver;

    private FirebaseFunctions mFunctions;

    private Preferences mPreference;

    private Player player1;
    private Player player2;

    private Tile sourceTile;
    private List<Tile> destinationTiles;

    private static boolean whiteAllianceTurn;

    private GridView chessBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        mFunctions = FirebaseFunctions.getInstance();

        mPreference = new Preferences(this);

        Intent intent = getIntent();
        if (intent.hasExtra(OPPONENT_EXTRA)) {
            com.ahmedmatem.android.chesstar.models.Player player =
                    intent.getParcelableExtra(OPPONENT_EXTRA);

            // player2 belongs to black alliance
            player2 = new Player(player.name, player.token, player.alliance);
            Alliance player1Alliance =
                    player2.getAlliance() == Alliance.WHITE ? Alliance.BLACK : Alliance.WHITE;
            // player1 belongs to white alliance
            player1 = new Player(mPreference.getName(), mPreference.getToken(), player1Alliance);

            whiteAllianceTurn = player1.getAlliance() == Alliance.WHITE;

            TextView player2Name = findViewById(R.id.player2);
            player2Name.setText(player2.getName() + " - " + !whiteAllianceTurn);
            TextView player1Name = findViewById(R.id.player1);
            player1Name.setText(player1.getName() + " - " + whiteAllianceTurn);
        }

        chessBoard = findViewById(R.id.chess_board);
        if(whiteAllianceTurn) {
            chessBoard.setAdapter(new TileAdapter(this, tiles));
        } else {
            chessBoard.setAdapter(new TileAdapter(this, Board.reverseTiles()));
        }

        chessBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(whiteAllianceTurn) {
                    Tile clickedTile = tiles.get(position);
                    if (clickedTile.isOccupied()) {
                        Piece clickedPiece = clickedTile.getPiece();
                        if(clickedPiece.getAlliance() == player1.getAlliance()) {
                            // white piece was selected
                            sourceTile = clickedTile;
                            destinationTiles =
                                    clickedPiece.calculateDestinationTiles(position, tiles);
                            updateBoardOnPieceSelected(sourceTile, destinationTiles);
                        } else {
                            // black piece was selected
                        }
                    } else {
                        if (destinationTiles != null && destinationTiles.contains(clickedTile)) {
                            // destination tile was clicked
                            int sourcePosition = sourceTile.getPosition();
                            int destinationPosition = position;
                            sourceTile.getPiece().move(sourcePosition, destinationPosition);
                            updateBoardOnMove();

                            // switch on/off player after its move
                            whiteAllianceTurn = !whiteAllianceTurn;
                            destinationTiles = null;

                            // send your move to the opponent
                            Map<String,String> data = new HashMap<>();
                            data.put("responseContent", RESPONSE_CONTENT_MOVE);
                            data.put("token", player2.getMessagingToken());
                            data.put("source", String.valueOf(sourcePosition));
                            data.put("destination", String.valueOf(destinationPosition));
                            mFunctions.getHttpsCallable("onMove").call(data);
                        }
                    }
                } else {
                    // black alliance turn
                    // TODO: implement black alliance turn
                }
            }
        });

        mMoveMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(SOURCE_EXTRA) && intent.hasExtra(DESTINATION_EXTRA)) {
                    int source = intent.getIntExtra(SOURCE_EXTRA, -1);
                    int destination = intent.getIntExtra(DESTINATION_EXTRA, -1);

                    if (whiteAllianceTurn) {

                    } else {
                        Tile sourceTile = tiles.get(source);
                        sourceTile.getPiece().move(source, destination);
                        updateBoardOnMove();
                    }
                }
            }
        };
    }

    private void updateBoardOnMove() {
        clearPreviousUpdate();
        // update chess board
        if(whiteAllianceTurn) {
            chessBoard.setAdapter(new TileAdapter(this, Board.getTiles()));
        } else {
            chessBoard.setAdapter(new TileAdapter(this, Board.reverseTiles()));
        }
    }

    private void updateBoardOnPieceSelected(Tile sourceTile, List<Tile> destinationTiles) {
        clearPreviousUpdate();

        sourceTile.isSelected = true;
        if(destinationTiles != null) {
            int destinationPosition;
            Tile destinationTile;
            for (Tile tile : destinationTiles) {
                destinationPosition = tile.getPosition();
                destinationTile = tiles.get(destinationPosition);
                if(!destinationTile.isOccupied()){
                    destinationTile.isPossibleDestination = true;
                }
            }
        }
        // update chess board
        ((TileAdapter)chessBoard.getAdapter()).setTiles(tiles);
    }

    private void clearPreviousUpdate() {
        Tile tile;
        for (int i = 0; i < 64; i++) {
            tile = tiles.get(i);
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
