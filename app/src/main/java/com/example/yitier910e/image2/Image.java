package com.example.yitier910e.image2;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Image extends AppCompatActivity {
    private Button resetButton;
    private Button undoButton;
    private Button applyButton;
    private ImageView imView;
    private Bitmap bmp;
    private Bitmap bmpCopy;
    private BitmapFactory.Options o;
    public Uri file;
    public static final int PHOTO = 1;
    public static final int GALLERY = 2;
    private String mCurrentPhotoPath ;

    //Fonction qui traite les actions liées au bouton apply
    private View.OnClickListener applyButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
        }

    };
    // Fonction qui traite les actions liées au bouton undo
    private View.OnClickListener undoButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
        }

    };
    //Fonction qui traite les actions liées au bouton reset
    private View.OnClickListener resetButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            bmp= bmpCopy.copy(bmpCopy.getConfig(),true);
            imView.setImageBitmap(bmp);
        }

    };
// Fonction qui retourne le fichier qui contient l'image, le fichier aura comme nom l'heure courante afin de ne pas avoir de doublon.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
// Fonction qui permet d'ouvrir la galerie afin de sélectionner la photo à afficher dans l'appli
    private void dispatchOpenGalleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY);

    }
// Fonction qui permete de lancer l'appareil photo et qui de créer le fichier pour enregistrer la futur image
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.yitier910e.image2.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PHOTO);
            }
        }
    }
    // Fonction qui permet d'ajouter une photo à la galerie
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    // Fonction qui permet d'ajouter une photo à la galerie
    private void saveImage(){
        String storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"blabla").toString();
        File myDir = new File(storageDir);
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".png";

        File file = new File(storageDir, imageFileName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // Fonction qui permet de mettre la bitmap dans l'imageView en prennant en compte les options afin de pouvoir la modifier
    private void setPic() {


        bmp = BitmapFactory.decodeFile(mCurrentPhotoPath, o);
        bmpCopy = BitmapFactory.decodeFile(mCurrentPhotoPath, o);
        imView.setImageBitmap(bmp);

    }



    @Override
    // Fonction principale des Intent qui permet de traiter les différents appel d'intent
    // Ceux ci sont gérés par la valeur du requestCode et du resultCode.
    // Cette fonction est appelée après l'utilisation de l'intent
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case (PHOTO):
                    galleryAddPic();
                    setPic();
                    saveImage();
                    break;

                case (GALLERY):
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            bmpCopy = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                            bmp=bmpCopy.copy(bmpCopy.getConfig(), true);
                            imView.setImageBitmap(bmp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                default:
                    break;
            }
        }
        else {

        }

        }



    // Fonctino qui permet de mettre en avant les contours d'une image en blanc
    private void contouring(Bitmap bmp){
        int outh = bmp.getHeight();
        int outw = bmp.getWidth();
        int[] pixels = new  int [outw*outh];
        toGray(bmp);

        int[] pixelsf = new int [outw*outh];
        bmp.getPixels(pixels, 0, outw,  0, 0, outw, outh);
        bmp.getPixels(pixelsf, 0, outw,  0, 0, outw, outh);
        int[] derX = {-1,-1,-1,0,0,0,1,1,1};
        int[] derY = {-1,0,1,-1,0,1,-1,0,1};
        int[] currentPixel = new  int [9];
        for (int k = 1; k < outw - 1; k++) {
            for (int l = 1; l < outh - 1; l++) {
                int sumX = 0;
                int sumY = 0;
                for (int m = 0; m < 3; m++) {
                    currentPixel[3*m] = Color.red(pixels[k - 1 + (l+m-1)*outw]);
                    currentPixel[3*m+1] = Color.red(pixels[k + (m-1+l)*outw]);
                    currentPixel[3*m+2] = Color.red(pixels[k + 1 + (m+l-1)*outw]);
                }
                for (int n = 0; n < 9; n++) {
                    sumX += currentPixel[n] * derX[n];
                    sumY += currentPixel[n] * derY[n];
                }
                int norm = (int) Math.min(Math.sqrt(sumX*sumX + sumY*sumY), 255);
                pixelsf[k + l*outw] = Color.rgb(norm, norm, norm);
            }
        }
        bmp.setPixels(pixelsf, 0, outw,  0, 0, outw, outh);
    }

    // Fonction qui permet d'appliquer une convolution qui aura pour but de flouter l'image en fonction de la matrice passée en paramètre.
    public void convol(Bitmap bmp, int[][] conv) {




        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        int[] newPixels = new int[w * h];
        bmp.getPixels(newPixels, 0, w, 0, 0, w, h);

        int length = conv.length;
        int l2 = (length - 1) / 2;
        int div=0;
        for(int i=0; i<length;i++){
            for(int j=0; j<length;j++){
                div+= conv[i][j];
            }
        }
        if(div==0)
            div=1;

        for (int i = l2; i < w - l2; i++) {
            for (int j = l2; j < h - l2; j++) {
                int summR=0;
                int summG=0;
                int summB=0;
                for (int k = 0; k < length; k++) {
                    for (int l = 0; l < length; l++) {
                        int c = pixels[i + k - l2 + (j + l - l2) * w];
                        summR += Color.red(c) * conv[k][l];
                        summG += Color.green(c) * conv[k][l];
                        summB += Color.blue(c) * conv[k][l];
                    }

                }
                summR/=div;
                summG/=div;
                summB/=div;
                newPixels[i+(j*w)] = Color.rgb(summR,summG,summB);
            }
        }
        bmp.setPixels(newPixels, 0, w, 0, 0,w , h);
    }
    // Fonction permettant de calculer les coefficients de la gaussienne
    double gauss(int x, double y, double sigma,int mu, double a){
        return a*Math.exp(-((Math.pow(x-mu,2))+Math.pow(y-mu,2))/(2*sigma*sigma));
    }

    // Fonction permettant de retourner la matrice de la Gaussienne en parcourant seuelement 1/8 des valeurs de la matrice
    int[][] ArrayGauss(int size,int a){

        int [][] tab= new int[size][size];
        int mu= (size-1)/2;
        double  sigma = mu/Math.sqrt((Math.log(a)*2));
        //double  sigma = Math.sqrt(Math.pow(mu, 2)/(Math.log(a)-1));
        for(int i= 0; i<=mu;i++){
            for(int j=0; j<=i;j++){
                int g= (int)gauss(i,j,sigma,mu,a);
                tab[i][j]=g;
                tab[j][i]=g;
                tab[2*mu-i][j]=g;
                tab[2*mu-j][i]=g;
                tab[i][2*mu-j]=g;
                tab[j][2*mu-i]=g;
                tab[2*mu-i][2*mu-j]=g;
                tab[2*mu-j][2*mu-i]=g;
            }
        }
        return tab;
    }


    // Fonction qui retourne l'histogramme d'une image
    public int[] histo(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] hist = new int[256];
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        for (int i = 0; i<w*h;i++){
            hist[Color.red(pixels[i])]++; //on suppose l'image déjà en gris
        }
        return hist;
    }
// Pourra être utilisé à un certain moment
    public int[] histoHSV(Bitmap bmp){
        int[] hist=new int[256];
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        for(int i=0;i< w*h;i++){
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            float[] hsv = new float[3];
            Color.RGBToHSV(r, g, b, hsv);
            hist[(int)(hsv[2]*255)]+=1;
        }
        return hist;
    }

// Met l'image en gris sauf la couleur passée dans le paramètre c
    public void toGrayExcept(Bitmap bmp, int c){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        float[] hsvC = new float[3];
        Color.colorToHSV(c, hsvC);
        for (int i = 0; i<w*h;i++){
            float[] hsv = new float[3];
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            Color.RGBToHSV(r, g, b, hsv);

            if (hsv[0]>hsvC[0]+20 || hsv[0]<hsvC[0]-20) {
                int gl = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                pixels[i] = Color.rgb(gl, gl, gl);
            }
        }
        bmp.setPixels(pixels, 0, w, 0, 0,w , h);
    }
    // Fonction qui permet de mettre l'image en noir et blanc
    public void toGray(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        for (int i = 0; i<w*h;i++){
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            int gl = (int) (r*0.3+g*0.59+b*0.11);
            pixels[i] = Color.rgb(gl,gl,gl);
        }
        bmp.setPixels(pixels, 0, w, 0, 0,w , h);
    }

    //prend un histogramme et renvoi une table d'asso
    public int[] egalisator(int[] hist) {
        int nbPix = 0;
        for (int i = 0; i < 256; i++) {
            nbPix += hist[i];
        }
        int[] tabAss = new int[256];
        int cpt = 0;
        for (int i = 0; i < 256; i++) {
            cpt += hist[i];
            tabAss[i] = cpt * 255 / nbPix;
        }
        return tabAss;
    }

    // Fonction qui applique la table d'association précédement calculé dans une image en niveau de gris
    public void applyTabAssGray(Bitmap bmp, int[] tabAss){

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        for (int i = 0; i<w*h;i++){
            int gray = Color.red(pixels[i]);
            pixels[i] = Color.rgb(tabAss[gray],tabAss[gray],tabAss[gray]);
        }
        bmp.setPixels(pixels, 0, w, 0, 0,w , h);
    }
    // Fonction qui remonte la luminosité de l'image en ajoutant alpha
    public void lumin(Bitmap bmp, int alpha){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        for (int i = 0; i<w*h;i++){
            pixels[i] = Color.rgb(Math.max(0,Math.min(255,Color.red(pixels[i])+alpha)),Math.max(0,Math.min(255,Color.green(pixels[i])+alpha)),Math.max(0,Math.min(255,Color.blue(pixels[i])+alpha)));
        }
        bmp.setPixels(pixels, 0, w, 0, 0,w , h);
    }
    // Fonction qui applique un coefficient alpha à la luminosité aillant pour effet de la rendre plus claire ou sombre en fonction de celui-ci
    public void expo(Bitmap bmp, double alpha){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        for (int i = 0; i<w*h;i++){
            int red = (int)(Color.red(pixels[i])*alpha);
            red = Math.min(255,red);
            int green = (int)(Color.green(pixels[i])*alpha);
            green = Math.min(255,green);
            int blue = (int)(Color.blue(pixels[i])*alpha);
            blue = Math.min(255,blue);
            pixels[i] = Color.rgb(red,green,blue);
        }
        bmp.setPixels(pixels, 0, w, 0, 0,w , h);
    }

    // Fonction qui transforme un pixel rgb en pixel hsv
    public float[] RGBToHSV(int pix){
        int r = Color.red(pix);
        int g = Color.green(pix);
        int b = Color.blue(pix);
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        return hsv;
    }




    // Fonction qui peut servir plus tard
    public void applyTabAssHSV(Bitmap bmp, int[] tabAss){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        for (int i = 0; i<w*h;i++){
            float[] hsv = RGBToHSV(pixels[i]);
            // int val = (int)(hsv[2]*255);
            hsv[2]=(float)tabAss[(int)(hsv[2]*255)]/255;
            pixels[i]= Color.HSVToColor(hsv);
        }
        bmp.setPixels(pixels, 0, w, 0, 0,w , h);
    }//omg complexité


    // Fonction qui peut servir plus tard

    public int[] castToHistHSV(Bitmap bmp){
        int[] hsv=new int[1000];
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w , 0,0,w,h);
        return hsv;
    }


    // Fonction extrêmement important qui permet de demander les permissions nécessaires afin d'enregistrer les images.
    private void checkPermissions() {
        int apiLevel = Build.VERSION.SDK_INT;
        String[] permissions;
        if (apiLevel < 16) {
            permissions = new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        ActivityCompat.requestPermissions(this,permissions, 0);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imView = (ImageView) findViewById(R.id.imgView);
        resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(resetButtonListener);

        applyButton = (Button) findViewById(R.id.applyButton);
        applyButton.setOnClickListener(applyButtonListener);

        undoButton = (Button) findViewById(R.id.undoButton);
        undoButton.setOnClickListener(undoButtonListener);



        o = new BitmapFactory.Options();
        o.inMutable = true;
        o.inScaled=true;

        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.lena,o);
        bmpCopy=BitmapFactory.decodeResource(getResources(),R.drawable.lena,o);
        imView.setImageBitmap(bmp);
        checkPermissions();

        //ArrayGauss(5,42);

    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_styley,menu);
        return true;
    }

    //Fonction qui gère tous les différent item du menu
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.grayId:
                toGray(bmp);
                break;
            case R.id.expoId:
                expo(bmp,1.5);
                break;
            case R.id.contourId:
                contouring(bmp);
                break;
            case R.id.luminId:
                lumin(bmp,50);
                break;
            case R.id.monoId:// on peut changer la couleur en replaçant Color.RED par celle que l'on souhaite
                toGrayExcept(bmp,Color.RED);
                break;
            case R.id.moyenneId: // a tester que sur la première image parce qu'elle est suffisament petit pour voir l'effet (image urbex)
                int[][] matrix = {{1,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1},
                        {1,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1}};
                convol(bmp,matrix);

                break;
            case R.id.equalId:

                applyTabAssGray(bmp,egalisator(histo(bmp)));
                break;

            case R.id.gaussId:
                convol(bmp, ArrayGauss(201,20));
                break;
            case R.id.photoId:
                dispatchTakePictureIntent();
                break;
            case R.id.loadId:
                dispatchOpenGalleryIntent();
                break;
            case R.id.saveId:
                saveImage();
                break;
            default:
                return false;


        }
        return true;
    }
}
