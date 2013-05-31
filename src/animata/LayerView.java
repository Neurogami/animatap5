package animata;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import animata.model.Layer;

public class LayerView {

  protected final PApplet applet;
  private final Layer layer;
  private MeshView mesh;
  private ArrayList<LayerView> layers;

  public LayerView(Layer layer, PApplet applet) {
    this.applet = applet;
    this.layer = layer;
    if (layer.mesh != null) {
      mesh = new MeshView(applet, layer);
    }
    addChildLayers(layer.layers);
  }

  public Layer getLayer(String layerName) {
     //System.err.println("LayerView#getLayer: "   + layerName ); // DEBUG

    Layer l = null;
    for (Layer ll : layer.layers) {
      // System.err.println("LayerView#getLayer: compare "   + layerName + " to " + ll.name); // DEBUG
      if (ll.name.equals(layerName)) {
        return ll;
      }
    }
    return l;
  }

 public void setNewMeshImage(PImage layerImage, String layerName ) {
    for (Layer llayer : layer.layers) {
      llayer.setNewTextureImage(applet, layerImage, layerName);
    }
  }


  public void setNewMeshImage(String imageName, String layerName ) {
    // System.err.println("LayerView#setNewMeshImage: " + imageName + " for " + layerName ); // DEBUG
    for (Layer llayer : layer.layers) {
      llayer.setNewTextureImage(applet, imageName, layerName);
    }
  }

  private void addChildLayers(ArrayList<Layer> layers) {
    this.layers = new ArrayList<LayerView>();
    for (Layer llayer : layers) {
      this.layers.add(new LayerView(llayer, applet));
    }
  }

  public void draw(float x, float y) {

    // System.err.println("LayerView#draw using: " + x + ", " + y + ". "); // DEBUG
    // System.err.println("LayerView#draw This layer has x, y " + layer.x() + ", " + layer.y() ); // DEBUG
    // This will propagate layer locatins down to child layers. Is this correct?
    // Is the location of child layers relative to the parent layer?
    // If you don;t do this then you find some scenes created in Animata make no sense becasue none of
    // the layer offsets are getting applied.
    
    x = x+layer.x();
    y = y+layer.y();

    // HOWEVER: doTransformation is adding in the layer x and y.
    // Yet without the above lines the placement of items in some in scenes is obvioulsy wrong.
    // 

    // How can we apply the change in x, y set in the Layer?
    // The current x,y seems to come direct from the sketch, in `draw` (e.g. Foo.draw(10, 20);)
    // We could grab the x,y stored in the layer and apply it

    applet.pushMatrix();
    doTransformation(x, y);
    if (mesh != null) {
      mesh.draw();
    }
    drawChildLayers(x, y); // This used to come AFTER popMatrix, but it seems that the paretn layer needs to pass on it's own scaling and such
    applet.popMatrix();

  }

  private void drawChildLayers(float x, float y) {
    for (LayerView layerView : layers) {
      if (layerView.layer.visible() ) { 
        // Each call to a layer draw does a scale.
        // However, there's no parent scale being passed on
        // That seems to be a bug. I C Animata, if you scale the owner layer
        // then the child layers are scaled as well.
        layerView.draw(x, y);
      }
    }
  }

  private void doTransformation(float _x, float _y) {
    //applet.translate(layer.x + _x, layer.y + _y, layer.z);
    // This, with the changes in draw, seems to work better
    applet.translate( _x, _y, layer.z);
    applet.scale(layer.scale, layer.scale, 1);
  }
}
