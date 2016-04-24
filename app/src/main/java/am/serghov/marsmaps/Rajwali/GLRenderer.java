package am.serghov.marsmaps.Rajwali;

import com.flipboard.bottomsheet.BottomSheetLayout;
import am.serghov.marsmaps.BottomSheet.CustomBottomSheet;
import am.serghov.marsmaps.Coordinate.latlon;
import am.serghov.marsmaps.Map.Map;
import  am.serghov.marsmaps.R;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.util.GLU;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;


public class GLRenderer extends RajawaliRenderer {

    public Context context;
    private DirectionalLight directionalLight;

    private Sphere marsSphere;
    private Sphere earthSphere;

    private boolean isHidden = false;

    private Plane skyPlane;

    private double rightAlpha = 1;
    private double rotationY = 0;

    private Map mainMap;

    private Plane scaleBase;
    private Plane scaleTop;
    private Plane scaleBottom;
    private Plane scaleMetricNum;
    private Plane scaleNonMetricNum;
    private Plane scaleShutter;

    private boolean scaleHidden;
    private CustomBottomSheet bottomSheet;

    Material scaleMetricMaterial;
    int prevMetricScaleNum = 0;

    int[] metricSizes = {2000000, 1000000, 500000, 250000, 100000, 50000, 25000, 10000, 5000, 2500};
    Texture[] scaleMetricTextures = {
            new Texture("MarsScale0", R.drawable.m2000000),
            new Texture("MarsScale1", R.drawable.m1000000),
            new Texture("MarsScale2", R.drawable.m500000),
            new Texture("MarsScale3", R.drawable.m250000),

            new Texture("MarsScale4", R.drawable.m100000),
            new Texture("MarsScale5", R.drawable.m50000),
            new Texture("MarsScale6", R.drawable.m25000),
            new Texture("MarsScale7", R.drawable.m10000),

            new Texture("MarsScale8", R.drawable.m5000),
            new Texture("MarsScale9", R.drawable.m2500),
    };

    Material scaleNonMetricMaterial;
    int prevNonMetricScaleNum = 0;

    int[] nonMetricSizes = {2000000, 1000000, 500000, 250000, 100000, 50000, 25000, 10000, 5000, 2500, 1000, 500};

    Texture[] scaleNonMetricTextures = {
            new Texture("NMMarsScale0", R.drawable.nm2000000),
            new Texture("NMMarsScale1", R.drawable.nm1000000),
            new Texture("NMMarsScale2", R.drawable.nm500000),
            new Texture("NMMarsScale3", R.drawable.nm250000),

            new Texture("NMMarsScale4", R.drawable.nm100000),
            new Texture("NMMarsScale5", R.drawable.nm50000),
            new Texture("NMMarsScale6", R.drawable.nm25000),
            new Texture("NMMarsScale7", R.drawable.nm10000),

            new Texture("NMMarsScale8", R.drawable.nm5000),
            new Texture("NMMarsScale9", R.drawable.nm2500),
    };


    public GLRenderer(Context context, Map mainMap) {
        super(context);
        this.context = context;
        setFrameRate(60);
        this.mainMap = mainMap;
    }

    @Override
    protected void initScene() {


        directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);

        getCurrentCamera().setFieldOfView(15);
        getCurrentCamera().setFarPlane(500000);
        //getCurrentCamera().setNearPlane(0.01f);

        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());

        material.setColor(0);

        Texture marsTexture = new Texture("Mars", R.drawable.mars_texture_small);
        try{
            material.addTexture(marsTexture);
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        marsSphere = new Sphere(1.0f, 24, 24);
        marsSphere.setMaterial(material);
        marsSphere.setTransparent(false);

        Material earthMaterial = new Material();
        earthMaterial.enableLighting(true);
        earthMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());

        earthMaterial.setColor(0);

        Texture earthTexture = new Texture("Mars", R.drawable.earth_texture);
        try{
            earthMaterial.addTexture(earthTexture);
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        earthSphere = new Sphere(1.9f, 24, 24);
        earthSphere.setMaterial(earthMaterial);
        earthSphere.setTransparent(true);

        /*Material skyMaterial = new Material();
        skyMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        skyMaterial.setColor(0);

        Texture skyTexture = new Texture("Mars", R.drawable.mars_atmosphere);
        try{
            skyMaterial.addTexture(skyTexture);
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        skyPlane = new Plane(5,5,1,1);
        skyPlane.setMaterial(skyMaterial);
        skyPlane.setTransparent(true);



        getCurrentScene().addChild(skyPlane);*/

        Material scaleBaseMaterial = new Material();
        scaleBaseMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        scaleBaseMaterial.setColor(0);
        try{
            scaleBaseMaterial.addTexture(new Texture("Mars", R.drawable.scale_base));
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        scaleBase = new Plane(300,60,1,1);

        scaleBase.setMaterial(scaleBaseMaterial);
        scaleBase.setTransparent(true);



        //scale base top
        Material scaleTopMaterial = new Material();
        scaleTopMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        scaleTopMaterial.setColor(0);
        Texture scaleTopTexture = new Texture("Mars", R.drawable.scale_head);
        try{
            scaleTopMaterial.addTexture(scaleTopTexture);
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        scaleTop = new Plane(8,28,1,1);

        scaleTop.setMaterial(scaleTopMaterial);
        scaleTop.setTransparent(true);

        //scale base bottom

        scaleBottom = new Plane(8,28,1,1);

        scaleBottom.setMaterial(scaleTopMaterial);
        scaleBottom.setTransparent(true);
        scaleBottom.setRotX(180);

        //getCurrentScene().addChild(skyPlane);


        //scale metric number
        scaleMetricMaterial = new Material();
        scaleMetricMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        scaleMetricMaterial.setColor(0);

        Texture scaleMetricTexture = new Texture("scale_metric", R.drawable.m2000000);
        try{
            scaleMetricMaterial.addTexture(scaleMetricTexture);
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        scaleMetricNum = new Plane(110,40,1,1);
        scaleMetricNum.setDoubleSided(true);
        scaleMetricNum.setRotY(180);
        scaleMetricNum.setMaterial(scaleMetricMaterial);
        scaleMetricNum.setTransparent(true);


        //scale non metric number
        scaleNonMetricMaterial = new Material();
        scaleNonMetricMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        scaleNonMetricMaterial.setColor(0);

        Texture scaleNonMetricTexture = new Texture("scale_non_metric", R.drawable.nm2000000);
        try{
            scaleNonMetricMaterial.addTexture(scaleNonMetricTexture);
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        scaleNonMetricNum = new Plane(110,40,1,1);
        scaleNonMetricNum.setDoubleSided(true);
        scaleNonMetricNum.setRotY(180);
        scaleNonMetricNum.setMaterial(scaleNonMetricMaterial);
        scaleNonMetricNum.setTransparent(true);

        //scale shutter

        Material scaleShutterMaterial = new Material();
        scaleShutterMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        scaleShutterMaterial.setColor(0);

        try{
            scaleShutterMaterial.addTexture(new Texture("scale_metric", R.drawable.scale_shutter));
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        scaleShutter = new Plane(2,8,1,1);
        scaleShutter.setMaterial(scaleShutterMaterial);
        scaleShutter.setTransparent(true);

        getCurrentScene().addChild(marsSphere);
        getCurrentScene().addChild(earthSphere);

        getCurrentScene().addChild(scaleBase);
        getCurrentScene().addChild(scaleTop);
        getCurrentScene().addChild(scaleBottom);
        getCurrentScene().addChild(scaleShutter);
        getCurrentScene().addChild(scaleMetricNum);
        getCurrentScene().addChild(scaleNonMetricNum);


        getCurrentCamera().setZ(80f);

    }

    public void setYRotation(double rotationY)
    {
        this.rotationY = rotationY;
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        double diff = (getScreenCoordinates(0,0).x - getScreenCoordinates(100,0).x)/100.0;

        double yOffset = 0;
        if (bottomSheet!=null)
        {
            yOffset = bottomSheet.getBottomSheetTranslation();
            //Log.e("TRANSLATION", "" + bottomSheet.getBottomSheetTranslation());
        }

        marsSphere.setScreenCoordinates(getViewportWidth() * rightAlpha,getViewportHeight() - yOffset, getViewportWidth(),getViewportHeight(),-getCurrentCamera().getZ());
        marsSphere.setPosition(marsSphere.getX()+2.3,marsSphere.getY()+2.5,marsSphere.getZ());

        earthSphere.setScreenCoordinates(getViewportWidth() * rightAlpha,getViewportHeight() - yOffset, getViewportWidth(),getViewportHeight(),-getCurrentCamera().getZ());
        earthSphere.setPosition(earthSphere.getX()+2.3,earthSphere.getY()+2.5,earthSphere.getZ());

        latlon rotation = mainMap.getCenterCoorinates();

        marsSphere.setRotation(new Vector3(0,1,0), 0);

        marsSphere.rotate(new Vector3(0,1,0),rotation.getLon());
        marsSphere.rotate(new Vector3(1,0,0),rotation.getLat());

        earthSphere.setRotation(new Vector3(0,1,0), 0);

        earthSphere.rotate(new Vector3(0,1,0),rotation.getLon());
        earthSphere.rotate(new Vector3(1,0,0),rotation.getLat());

        //Log.e("longitude", ""+rotation.getLon());
        if (!scaleHidden) {
            scaleBase.setVisible(true);
            scaleTop.setVisible(true);
            scaleBottom.setVisible(true);
            scaleShutter.setVisible(true);
            scaleMetricNum.setVisible(true);
            scaleNonMetricNum.setVisible(true);
            drawScaleMeter();
        }
        else
        {
            scaleBase.setVisible(false);
            scaleTop.setVisible(false);
            scaleBottom.setVisible(false);
            scaleShutter.setVisible(false);
            scaleMetricNum.setVisible(false);
            scaleNonMetricNum.setVisible(false);
        }

    }

    private void drawScaleMeter()
    {
        double diff = (getScreenCoordinates(0,0).x - getScreenCoordinates(100,0).x)/100.0;
        double scaleBaseWidth = diff * dpToPixel(130);


        scaleBase.setPosition(getScreenCoordinates(getViewportWidth()*0,getViewportHeight()));

        scaleBase.setScale(diff);
        scaleBase.setScaleX(scaleBaseWidth/300.0);

        scaleBase.moveRight(-scaleBaseWidth/2.0 - dpToPixel(56+16+16)*diff); // take 16+16 for margins
        scaleBase.moveUp(30*diff + dpToPixel(32+16) * diff);

        scaleTop.setPosition(scaleBase.getPosition());
        scaleTop.setScale(diff);
        scaleTop.moveUp(14*diff);



        scaleBottom.setPosition(scaleBase.getPosition());
        scaleBottom.setScale(diff);
        scaleBottom.moveUp(14*diff);




        double curCircumference = 2 * Math.PI *Math.cos(Math.toRadians(Math.abs(mainMap.getCenterCoorinates().getLat()))) * 3390000.0;
        //Log.e("circumference", ""+curCircumference);

        for (int i=0;i<metricSizes.length;i++)
        {
            double cur = mainMap.cWidth * metricSizes[i] * mainMap.tileView.getScale() / curCircumference;
            if (cur*diff < scaleBaseWidth*0.92 && cur > 110 ) {
                scaleTop.moveRight(scaleBaseWidth/2.0 - cur * diff);
                if (prevMetricScaleNum==i)
                    break;

                prevMetricScaleNum = i;
                try {
                    scaleMetricMaterial.removeTexture(scaleMetricMaterial.getTextureList().get(0));

                    scaleMetricMaterial.addTexture(scaleMetricTextures[i]);
                    scaleMetricNum.setMaterial(scaleMetricMaterial);

                } catch (ATexture.TextureException e) {
                    //e.printStackTrace();
                }

                break;
            }
        }



        for (int i=0;i<nonMetricSizes.length;i++)
        {
            double cur = mainMap.cWidth * nonMetricSizes[i] * mainMap.tileView.getScale() * 1.609344 / curCircumference;
            if (cur > 110 && scaleBaseWidth - cur * diff > scaleBaseWidth * 0.08) {
                scaleBottom.moveRight(-scaleBaseWidth/2.0 + cur * diff);

                if (prevNonMetricScaleNum==i)
                    break;

                prevNonMetricScaleNum = i;
                try {
                    scaleNonMetricMaterial.removeTexture(scaleNonMetricMaterial.getTextureList().get(0));

                    scaleNonMetricMaterial.addTexture(scaleNonMetricTextures[i]);
                    scaleNonMetricNum.setMaterial(scaleNonMetricMaterial);

                } catch (ATexture.TextureException e) {
                    //e.printStackTrace();
                }

                break;
            }
        }



        scaleMetricNum.setPosition(scaleBase.getPosition());
        scaleMetricNum.setScale(diff);
        scaleMetricNum.moveUp(20*diff);
        scaleMetricNum.moveRight(55*diff);
        scaleMetricNum.moveRight(-scaleBaseWidth/2.0);

        scaleNonMetricNum.setPosition(scaleBase.getPosition());
        scaleNonMetricNum.setScale(diff);
        scaleNonMetricNum.moveUp(-20*diff);
        scaleNonMetricNum.moveRight(55*diff);
        scaleNonMetricNum.moveRight(-scaleBaseWidth/2.0);

        double newScaleSize = Math.max(scaleBase.getWorldPosition().x + scaleBaseWidth/2.0 - scaleTop.getWorldPosition().x,
                scaleBase.getWorldPosition().x + scaleBaseWidth/2.0 - scaleBottom.getWorldPosition().x) + 3*diff;

        scaleBase.setScaleX(newScaleSize/300.0);
        scaleBase.setX(getScreenCoordinates(0,getViewportHeight()).x);
        scaleBase.moveRight(-newScaleSize/2.0 - dpToPixel(56+16+16)*diff); // take 16+16 for margins

        scaleShutter.setPosition(scaleBase.getPosition());
        scaleShutter.setScale(diff);
        scaleShutter.moveRight(-newScaleSize/2.0);

    }

    private double dpToPixel(double dp)
    {
        return context.getResources().getDimensionPixelSize(R.dimen.hundreeddp) * dp / 100.0;
    }

    public Vector3 getScreenCoordinates(double x, double y)
    {
        return getScreenCoordinates(x,y,getViewportWidth(),getViewportHeight(),-getCurrentCamera().getZ(), getCurrentCamera().getProjectionMatrix());
    }

    public Vector3 getScreenCoordinates(double x, double y, int viewportWidth, int viewportHeight, double eyeZ, Matrix4 mPMatrix) {
        double[] r1 = new double[16];
        int[] viewport = new int[] { 0, 0, viewportWidth, viewportHeight };
        double[] modelMatrix = new double[16];
        Matrix.setIdentityM(modelMatrix, 0);

        GLU.gluUnProject(x, viewportHeight - y, 0.0, modelMatrix, 0, mPMatrix.getDoubleValues(), 0, viewport, 0, r1, 0);
        //setPosition(r1[0] * eyeZ, r1[1] * -eyeZ, 0);
        return new Vector3(r1[0] * eyeZ, r1[1] * -eyeZ, 0);
    }

    public void moveToRight(double alpha)
    {
        rightAlpha = alpha;
    }

    public void hide()
    {
        isHidden = true;
    }
    public void show()
    {
        isHidden = false;
    }


    public void showScale() {scaleHidden = false;}
    public void hideScale() {scaleHidden = true;}


    public void setBottomSheet(CustomBottomSheet bottomSheet)
    {
        this.bottomSheet = bottomSheet;
    }

    public void onTouchEvent(MotionEvent event){
    }

    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){
    }

}
