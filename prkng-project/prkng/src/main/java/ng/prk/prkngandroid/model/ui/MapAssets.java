package ng.prk.prkngandroid.model.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class MapAssets {
    private final static String TAG = "MapAssets";
    private final static float X_CENTER = 0.5f;
    // TODO Replace ic_maps_lot_* assets to stop relying on this magical number
    @Deprecated
    private final static float Y_CENTER = 0.5626f;

    private int lineWidth;
    private int lineColorFree;
    private int lineColorPaid;
    private int lineColorSelected;
    private String[] carshareLotsCompanies;
    private String[] carshareVehiclesCompanies;
    private Icon markerIconFree;
    private Icon markerIconPaid;
    private Icon markerIconSelected;
    private Icon markerIconTransparent;
    private Icon markerIconCarshareCommunauto;
    private Icon markerIconCarshareAutomobile;
    private Icon markerIconCarshareCar2go;
    private Icon markerIconCarshareZipcar;

    private Bitmap markerBitmapClosed;
    private Bitmap markerBitmapOpen;
    private Bitmap markerBitmapOpenBest;
    private Bitmap markerBitmapSelected;

    private SparseArray<Icon> closedMarkersCache;
    private SparseArray<Icon> openMarkersCache;
    private SparseArray<Icon> openBestMarkersCache;
    private SparseArray<Icon> selectedMarkersCache;

    private int markerTextSize;
    private String markerTextTemplate;

    private IconFactory iconFactory;

    public MapAssets(MapView mapView) {
        final Context context = mapView.getContext();
        final Resources res = mapView.getResources();

        iconFactory = mapView.getIconFactory();

        markerIconFree = iconFactory.fromResource(R.drawable.ic_spot_free);
        markerIconPaid = iconFactory.fromResource(R.drawable.ic_spot_paid);
        markerIconSelected = iconFactory.fromResource(R.drawable.ic_spot_selected);
        markerIconTransparent = iconFactory.fromResource(R.drawable.ic_spot_transparent);

        markerIconCarshareCommunauto = iconFactory.fromResource(R.drawable.ic_maps_carshare_communauto);
        markerIconCarshareAutomobile = iconFactory.fromResource(R.drawable.ic_maps_carshare_automobile);
        markerIconCarshareCar2go = iconFactory.fromResource(R.drawable.ic_maps_carshare_car2go);
        markerIconCarshareZipcar = iconFactory.fromResource(R.drawable.ic_maps_carshare_zipcar);

        lineColorFree = ContextCompat.getColor(context, R.color.map_line_free_spot);
        lineColorPaid = ContextCompat.getColor(context, R.color.map_line_paid_spot);
        lineColorSelected = ContextCompat.getColor(context, R.color.map_line_selected_spot);
        lineWidth = context.getResources().getDimensionPixelSize(R.dimen.map_line_width);
        carshareLotsCompanies = context.getResources().getStringArray(R.array.carshare_lots);
        carshareVehiclesCompanies = context.getResources().getStringArray(R.array.carshare_vehicles);

        markerTextSize = res.getDimensionPixelSize(R.dimen.lot_marker_text);
        markerTextTemplate = res.getString(R.string.currency_round);
        markerBitmapClosed = BitmapFactory.decodeResource(res, R.drawable.ic_maps_lot_closed);
        markerBitmapOpen = BitmapFactory.decodeResource(res, R.drawable.ic_maps_lot_open);
        markerBitmapOpenBest = BitmapFactory.decodeResource(res, R.drawable.ic_maps_lot_open_best);
        markerBitmapSelected = BitmapFactory.decodeResource(res, R.drawable.ic_maps_lot_selected);

        closedMarkersCache = new SparseArray<>();
        openMarkersCache = new SparseArray<>();
        openBestMarkersCache = new SparseArray<>();
        selectedMarkersCache = new SparseArray<>();
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public int getLineColorFree() {
        return lineColorFree;
    }

    public int getLineColorPaid() {
        return lineColorPaid;
    }

    public int getLineColorSelected() {
        return lineColorSelected;
    }

    public Icon getMarkerIconFree() {
        return markerIconFree;
    }

    public Icon getMarkerIconPaid() {
        return markerIconPaid;
    }

    public Icon getMarkerIconSelected() {
        return markerIconSelected;
    }

    public Icon getMarkerIconTransparent() {
        return markerIconTransparent;
    }

    public String[] getCarshareLotsCompanies() {
        return carshareLotsCompanies;
    }

    public String[] getCarshareVehiclesCompanies() {
        return carshareVehiclesCompanies;
    }

    public Icon getCarshareVehicleMarkerIcon(String company) {
        switch (company) {
            case Const.ApiValues.CARSHARE_COMPANY_COMMUNAUTO:
                return markerIconCarshareCommunauto;
            case Const.ApiValues.CARSHARE_COMPANY_AUTOMOBILE:
                return markerIconCarshareAutomobile;
            case Const.ApiValues.CARSHARE_COMPANY_CAR2GO:
                return markerIconCarshareCar2go;
            case Const.ApiValues.CARSHARE_COMPANY_ZIPCAR:
                return markerIconCarshareZipcar;
        }
        return null;
    }

    public Icon getLotMarkerIcon(int price, int type, boolean isBest) {
        // First, verify if same icon exists in cache
        final Icon cachedIcon = getCacheIcon(price, type, isBest);
        if (cachedIcon != null) {
            return cachedIcon;
        }

        // Copy to a mutable Bitmap
        Bitmap bitmap;
        if (isBest) {
            bitmap = markerBitmapOpenBest.copy(markerBitmapOpenBest.getConfig(), true);
        } else if (type == Const.BusinnessHourType.CLOSED) {
            bitmap = markerBitmapClosed.copy(markerBitmapClosed.getConfig(), true);
        } else {
            bitmap = markerBitmapOpen.copy(markerBitmapOpen.getConfig(), true);
        }

        // Create Paint
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(markerTextSize);

        // Draw text on Canvas, center-aligned
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawText(String.format(markerTextTemplate, price),
                X_CENTER * bitmap.getWidth(),
                Y_CENTER * bitmap.getHeight(),
                paint);

        final Icon icon = iconFactory.fromBitmap(bitmap);

        // Save icon to cache
        setCacheIcon(icon, price, type, isBest);

        return icon;
    }

    private void setCacheIcon(Icon icon, int price, int type, boolean isBest) {
        if (isBest) {
            openBestMarkersCache.append(price, icon);
        } else if (type == Const.BusinnessHourType.CLOSED) {
            closedMarkersCache.append(price, icon);
        } else {
            openMarkersCache.append(price, icon);
        }
    }

    private Icon getCacheIcon(int price, int type, boolean isBest) {
        if (isBest) {
            return openBestMarkersCache.get(price, null);
        } else if (type == Const.BusinnessHourType.CLOSED) {
            return closedMarkersCache.get(price, null);
        } else {
            return openMarkersCache.get(price, null);
        }
    }
}