package lu.uni.timetable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.io.InputStream;

import sysu.mobile.limk.library.indoormapview.Position;

/**
 * A View for an image map.
 * Extends MapView so as to prevent the user from moving the location pin.
 */
class CustomMapView extends sysu.mobile.limk.library.indoormapview.MapView {
    private Context c;
    private int previousPointerCount = 0;
    private boolean locationMoved = false;
    int locationSymbolWidth, locationSymbolHeight;
    Rect locationSymbolRect = new Rect(0, 0, 0, 0);
    boolean initialised = false;


    public CustomMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        c = context;
    }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
    }


    @Override
    public void initNewMap(InputStream inputStream, double scale, double rotation, Position currentPosition) {
        Bitmap symbolBitmap = BitmapFactory.decodeResource(c.getResources(), R.mipmap.marker);
        locationSymbolWidth = symbolBitmap.getWidth();
        locationSymbolHeight = symbolBitmap.getHeight();
        initialised = true;

        super.initNewMap(inputStream, scale, rotation, currentPosition);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                locationMoved = false;
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_POINTER_UP:
                previousPointerCount = event.getPointerCount();
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                float[] xy = {event.getX(), event.getY()};
                xy = transformToMapCoordinate(xy);
                if (isPointInRect(xy[0], xy[1], locationSymbolRect))
                    locationMoved = true;
                previousPointerCount = event.getPointerCount();
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                if (previousPointerCount == 1 && event.getPointerCount() == 1) {
                    if (locationMoved) {
                        return false;
                    }
                }
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLocationSymbolRect();
    }

    private void setLocationSymbolRect() {
        if(!initialised) return;

        Position pos = getRealLocation();
        int left = (int) (pos.getX() - locationSymbolWidth / 2);
        int right = left + locationSymbolWidth;
        int top = (int) (pos.getY() - locationSymbolHeight);
        int bottom = top + locationSymbolHeight;
        locationSymbolRect.set(left, top, right, bottom);
    }

    private boolean isPointInRect(float x, float y, Rect rect) {
        return rect != null && x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }
}
