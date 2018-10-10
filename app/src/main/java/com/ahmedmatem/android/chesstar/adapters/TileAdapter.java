package com.ahmedmatem.android.chesstar.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ahmedmatem.android.chesstar.R;
import com.ahmedmatem.chess.engine.board.Tile;
import com.ahmedmatem.chess.engine.pieces.Piece;

import java.util.Map;

public class TileAdapter extends BaseAdapter {
    private Context context;
    private Map<Integer,Tile> tiles;

    public TileAdapter(Context context, Map<Integer,Tile> tiles) {
        this.context = context;
        this.tiles = tiles;
    }

    @Override
    public int getCount() {
        return tiles.size();
    }

    @Override
    public Object getItem(int position) {
        return tiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TileViewHolder tileViewHolder;

        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.board_tile, parent, false);

            tileViewHolder = new TileViewHolder();
            tileViewHolder.content = (ImageView) convertView.findViewById(R.id.tile_content);

            convertView.setTag(tileViewHolder);
        } else {
            tileViewHolder = (TileViewHolder) convertView.getTag();
        }

        tileViewHolder.content.getLayoutParams().height = parent.getHeight() / 8;
        int drawablePieceIndex = getDrawablePieceIndex(position);

        TypedArray drawablePieceArray =
                context.getResources().obtainTypedArray(R.array.drawable_pieces);
        if(drawablePieceIndex != -1) {
            tileViewHolder.content.setImageResource(
                    drawablePieceArray.getResourceId(drawablePieceIndex, -1));
        }

        return convertView;
    }

    private int getDrawablePieceIndex(int position) {
        Tile tile = tiles.get(position);
        if(!tile.isOccupied()){
            return -1;
        }
        Piece piece = tile.getPiece();
        switch (piece.getCode()) {
            case ROOK_WHITE:
                return 0;
            case KNIGHT_WHITE:
                return 1;
            case BISHOP_WHITE:
                return 2;
            case QUEEN_WHITE:
                return 3;
            case KING_WHITE:
                return 4;
            case PAWN_WHITE:
                return 5;
            case ROOK_BLACK:
                return 6;
            case KNIGHT_BLACK:
                return 7;
            case BISHOP_BLACK:
                return 8;
            case QUEEN_BLACK:
                return 9;
            case KING_BLACK:
                return 10;
            case PAWN_BLACK:
                return 11;
            default:
                return -1;
        }
    }

    static class TileViewHolder{
        ImageView content;
    }
}
