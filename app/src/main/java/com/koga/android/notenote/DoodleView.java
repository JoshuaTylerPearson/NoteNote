
package com.koga.android.notenote;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.print.PrintHelper;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;import com.koga.android.notenote.R;

// the main screen that is painted
@SuppressLint({ "UseSparseArrays", "ClickableViewAccessibility", "DrawAllocation" })
public class DoodleView extends View 
{
   // used to determine whether user moved a finger enough to draw again   
   private static final float TOUCH_TOLERANCE = 10;

    private DataBase db;

   private Bitmap bitmap; // drawing area for display or saving
   private Canvas bitmapCanvas; // used to draw on bitmap   
   protected int backgroundColor = Color.TRANSPARENT;//white
   private final Paint paintScreen; // used to draw bitmap onto screen
   private final Paint paintLine; // used to draw lines onto bitmap
   protected boolean erase = false;
    private Context context;

   //protected int colorB4Erase;
   
   // Maps of current Paths being drawn and Points in those Paths
   private final Map<Integer, Path> pathMap = new HashMap<Integer, Path>(); 
   private final Map<Integer, Point> previousPointMap = 
      new HashMap<Integer, Point>();

   // used to hide/show system bars 
   private GestureDetector singleTapDetector;
      
   // DoodleView constructor initializes the DoodleView
   public DoodleView(Context context, AttributeSet attrs)  
   {
      super(context, attrs); // pass context to View's constructor
       this.context = context;
      hideSystemBars();
      showSystemBars();
      paintScreen = new Paint(); // used to display bitmap onto screen
      //if(this.getWidth() > 0 && this.getHeight() > 0)    	  
    	 // bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
      // set the initial display settings for the painted line
      paintLine = new Paint();
      paintLine.setAntiAlias(true); // smooth edges of drawn line
      paintLine.setColor(Color.BLACK); // default color is black
      paintLine.setStyle(Paint.Style.STROKE); // solid line
      paintLine.setStrokeWidth(5); // set the default line width
      paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends
      this.setBackgroundColor(Color.WHITE);
      erase = false;
      // GestureDetector for single taps
      singleTapDetector = 
         new GestureDetector(getContext(), singleTapListener);

      db = new DataBase(getRootView().getContext());

       //bitmap = db.getBitmaps();

       Bitmap workingBitmap= db.getBitmaps();
       bitmap = workingBitmap.copy(workingBitmap.getConfig(), true);
       bitmapCanvas = new Canvas(bitmap);
       bitmapCanvas.drawBitmap(bitmap, 0, 0, paintScreen);
       invalidate();
   }

   // Method onSizeChanged creates Bitmap and Canvas after app displays
   @Override 
   public void onSizeChanged(int w, int h, int oldW, int oldH)
   {
       //try {
          // bitmap = Bitmap.createBitmap(db.getBitmaps());
       Bitmap workingBitmap = db.getBitmaps();
       bitmap = workingBitmap.copy(workingBitmap.getConfig(), true);
           //Toast.makeText(context, "derp", Toast.LENGTH_LONG).show();
           bitmapCanvas = new Canvas(bitmap);
           bitmap.eraseColor(Color.TRANSPARENT);
       //}catch (Exception ignored){

       //}
       /*
	   if(bitmap == null)
	   {
		   //bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
		   bitmapCanvas = new Canvas(bitmap);
		   bitmap.eraseColor(Color.TRANSPARENT);
	   }
       */
       invalidate();
       // erase the Bitmap with white
   } 
   
   public void setBackground(int color)
   { 
	   /*
	   for(int x = 0; x < bitmap.getWidth(); x++)
	   {
		   for(int y = 0; y < bitmap.getHeight(); y++)
		   {
			   if(bitmap.getPixel(x, y) == backgroundColor)
				   bitmap.setPixel(x, y, color);
		   }
	   }*/
	   this.setBackgroundColor(color);
	   backgroundColor = color;	    
	   invalidate();
	 
   }
   // clear the painting
   public void clear()
   {
      pathMap.clear(); // remove all paths
      previousPointMap.clear(); // remove all previous points
      bitmap.eraseColor(Color.TRANSPARENT); // clear the bitmap 
      setBackground(Color.WHITE);
      backgroundColor = Color.WHITE;
      erase = false;
      invalidate(); // refresh the screen
   }
   
   // set the painted line's color
   public void setDrawingColor(int color) 
   {
	  if(erase)
		  erase = false;
	
	  paintLine.setColor(color);
      invalidate();
   } 

   // return the painted line's color
   public int getDrawingColor() 
   {
	  if(erase){		 
		  erase = false;	
		  return(Color.BLACK);
	  }
	  else 
	  return paintLine.getColor();
   }

   // set the painted line's width
   public void setLineWidth(int width) 
   {
      paintLine.setStrokeWidth(width);
   } 

   // return the painted line's width
   public int getLineWidth() 
   {
      return (int) paintLine.getStrokeWidth();
   } 

   // called each time this View is drawn
   @Override
   protected void onDraw(Canvas canvas) 
   {

      if (erase)
	  {	    	  
    	  paintLine.setColor(backgroundColor);
		  paintLine.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		  // for each path currently being drawn
	  }
	  else	  
		   paintLine.setXfermode(null);
      
      canvas.drawBitmap(bitmap, 0, 0, null);
	  
      for (Integer key : pathMap.keySet()) 
		  bitmapCanvas.drawPath(pathMap.get(key), paintLine); // draw line
   }

   // hide system bars and action bar
   public void hideSystemBars()
   {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
         setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_FULLSCREEN
            );
      
   }

   // show system bars and action bar
   public void showSystemBars()
   {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
         setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
   }
   
   public void setErase()
   {   
	   erase = true;
	   paintLine.setColor(backgroundColor);	
   }
 
   // create SimpleOnGestureListener for single tap events
   private SimpleOnGestureListener singleTapListener =  
      new SimpleOnGestureListener()
      {
         @Override
         public boolean onSingleTapUp(MotionEvent e)
         {
            if ((getSystemUiVisibility() & 
               View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0)
               hideSystemBars();
            else
               showSystemBars();
               
            return true;
         }            
      };

   // handle touch event
   @Override
   public boolean onTouchEvent(MotionEvent event) 
   {
	   
      // if a single tap event occurred on KitKat or higher device      
      if (singleTapDetector.onTouchEvent(event))
         return true;
      
      // get the event type and the ID of the pointer that caused the event
      int action = event.getActionMasked(); // event type 
      int actionIndex = event.getActionIndex(); // pointer (i.e., finger)
      
      // determine whether touch started, ended or is moving
      if (action == MotionEvent.ACTION_DOWN ||
         action == MotionEvent.ACTION_POINTER_DOWN) 
      {
         touchStarted(event.getX(actionIndex), event.getY(actionIndex), 
            event.getPointerId(actionIndex));
      } 
      else if (action == MotionEvent.ACTION_UP ||
         action == MotionEvent.ACTION_POINTER_UP) 
      {
         touchEnded(event.getPointerId(actionIndex));
      } 
      else 
      {
         touchMoved(event); 
      }
      
      invalidate(); // redraw
      return true;
   } // end method onTouchEvent

   // called when the user touches the screen
   private void touchStarted(float x, float y, int lineID) 
   {

      Path path; // used to store the path for the given touch id
      Point point; // used to store the last point in path
      
      // if there is already a path for lineID
      if (pathMap.containsKey(lineID)) 
      {
         path = pathMap.get(lineID); // get the Path
         path.reset(); // reset the Path because a new touch has started
         point = previousPointMap.get(lineID); // get Path's last point
      }
      else 
      {
         path = new Path(); 
         pathMap.put(lineID, path); // add the Path to Map
         point = new Point(); // create a new Point
         previousPointMap.put(lineID, point); // add the Point to the Map
      }

      // move to the coordinates of the touch
      
      path.moveTo(x, y);
      point.x = (int) x;
      point.y = (int) y;
   } // end method touchStarted

   // called when the user drags along the screen
   private void touchMoved(MotionEvent event) 
   {

      // for each of the pointers in the given MotionEvent
      for (int i = 0; i < event.getPointerCount(); i++) 
      {
         // get the pointer ID and pointer index
         int pointerID = event.getPointerId(i);
         int pointerIndex = event.findPointerIndex(pointerID);
            
         // if there is a path associated with the pointer
         if (pathMap.containsKey(pointerID)) 
         {
            // get the new coordinates for the pointer
            float newX = event.getX(pointerIndex);
            float newY = event.getY(pointerIndex);
            
            // get the Path and previous Point associated with 
            // this pointer
            Path path = pathMap.get(pointerID);
            Point point = previousPointMap.get(pointerID);
            
            // calculate how far the user moved from the last update
            float deltaX = Math.abs(newX - point.x);
            float deltaY = Math.abs(newY - point.y);

            // if the distance is significant enough to matter
            if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) 
            {
               // move the path to the new location
               path.quadTo(point.x, point.y, (newX + point.x) / 2,
                  (newY + point.y) / 2);

               // store the new coordinates
               point.x = (int) newX;
               point.y = (int) newY;
            } 
         } 
      }
   } // end method touchMoved

   // called when the user finishes a touch
   private void touchEnded(int lineID) 
   {
	   
      Path path = pathMap.get(lineID); // get the corresponding Path
      bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
      path.reset(); // reset the Path
   } 

   // save the current image to the Gallery
   public void saveImage()
   {
       db.addBitmap(bitmap);
      /*
      String name = "DroidNoteDraw" + System.currentTimeMillis() + ".jpg";
      
      // insert the image in the device's gallery
      String location = MediaStore.Images.Media.insertImage(
         getContext().getContentResolver(), bitmap, name, 
         "DroidNoteDraw Drawing");

      if (location != null) // image was saved
      {
         // display a message indicating that the image was saved
         Toast message = Toast.makeText(getContext(), 
            R.string.message_saved, Toast.LENGTH_SHORT);
         message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
            message.getYOffset() / 2);
         message.show();
      }
      else      
      {
         // display a message indicating that the image was saved
         Toast message = Toast.makeText(getContext(), 
            R.string.message_error_saving, Toast.LENGTH_SHORT);
         message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
            message.getYOffset() / 2);
         message.show(); 
      }
      */
   } // end method saveImage

} // end class DoodleView

