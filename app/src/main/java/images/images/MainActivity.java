package images.images;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    Button getImage;
    ImageView imageView;
    TextView path;
    ImageView thumnail;
    TextView base_big;
    TextView base_small;
    private final static int RESULT_SELECT_IMAGE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = "GalleryUtil";

    String mCurrentPhotoPath;
    File photoFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getImage = (Button)findViewById(R.id.get_image);
        imageView = (ImageView)findViewById(R.id.imageView);
        thumnail = (ImageView)findViewById(R.id.thumbnail);
        path = (TextView) findViewById(R.id.path);
        base_big = (TextView) findViewById(R.id.base_big);
        base_small = (TextView) findViewById(R.id.base_small);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //Pick Image From Gallery
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_SELECT_IMAGE);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case RESULT_SELECT_IMAGE:

                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    try{
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Intent returnFromGalleryIntent = new Intent();
                        returnFromGalleryIntent.putExtra("picturePath", picturePath);
                        setResult(RESULT_OK, returnFromGalleryIntent);

                        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        path.setText(picturePath);

                        Bitmap  thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(picturePath), 50, 50);
                        thumnail.setImageBitmap(thumb);
                       // base_big.setText("Big image base 64 " + encodeTobase64(BitmapFactory.decodeFile(picturePath)));
                        base_small.setText("Small image base 64 " +encodeTobase64(thumb));

                    }catch(Exception e){
                        e.printStackTrace();
                        Intent returnFromGalleryIntent = new Intent();
                        setResult(RESULT_CANCELED, returnFromGalleryIntent);
                        finish();
                    }
                }else{
                    Log.i(TAG, "RESULT_CANCELED");
                    Intent returnFromGalleryIntent = new Intent();
                    setResult(RESULT_CANCELED, returnFromGalleryIntent);
                    finish();
                }
                break;
        }
    }
    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
