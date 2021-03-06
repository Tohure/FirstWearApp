package pe.tohure.firstwearapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DataItemResult>,
        View.OnClickListener {

    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient;
    private Button sendStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);
        sendStep = findViewById(R.id.sendStep);
        sendStep.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return Asset.createFromBytes(byteArrayOutputStream.toByteArray());
    }

    public void sendStepCount(int steps, long timestamp) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.lorem_image);
        Asset asset = createAssetFromBitmap(icon);

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/step-counter");
        putDataMapRequest.getDataMap().putInt("step-count", steps);
        putDataMapRequest.getDataMap().putLong("timestamp", timestamp);
        putDataMapRequest.getDataMap().putAsset("profile-image", asset);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request).setResultCallback(this);
    }

    private void sendMessage(Node node) {
        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/path/message", new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            //FAILED MESSAGE
                        }
                    }
                }
        );
    }

    private void getNodeApiID() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            sendMessage(node);
        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        sendStepCount(10, 1502773150);
    }

    @Override
    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
        if (!dataItemResult.getStatus().isSuccess()) {
            Toast.makeText(this, "No service", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Si service", Toast.LENGTH_SHORT).show();
        }
    }


}
