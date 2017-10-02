package com.example.susi.camera2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CameraHelpDevActivity extends AppCompatActivity implements View.OnClickListener, Camera.ShutterCallback, Camera.PictureCallback {
    private CameraController cameraController;
    private boolean emCamera;
    private Button botaoCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_help_dev);
        emCamera = true;
        cameraController = new CameraController(this, R.id.area_view);

        botaoCamera = (Button) findViewById(R.id.bt_fotografar);
        botaoCamera.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_fotografar:
                if (emCamera) {
                    cameraController.tirarFoto(this, null, this);
                } else {
                    emCamera = true;
                    botaoCamera.setText("Fotografar");
                    cameraController.iniciarVisualizacao();
                }
                break;
        }
    }

    /**
     * Ação do click tirar foto
     */
    public void onShutter() {
        botaoCamera.setText("Fotografar 2");
        emCamera = false;
    }

    /**
     * Retorno da ação de tirar foto
     *
     * @param bytes
     * @param camera
     */
    public void onPictureTaken(byte[] bytes, Camera camera) {

        Bitmap foto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        cameraController.pararVisualizacao();
    }
}
