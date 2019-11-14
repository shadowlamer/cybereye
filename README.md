# Cyber Eye

This is simple 3d engine written in java in 2001 (may be my first java program). 
Source was lost and reverse engineered. It can render 3d models in .ASC format.
I'm just going to leave this here.

<img src="https://raw.githubusercontent.com/shadowlamer/cybereye/master/screenshot.png" height="200"/>

### Compiling

```
javac src/ru/anhot/archive/cybereye/Cybereye.java -d webroot
```

### Running

```
appletviewer webroot/index.html
```

### Applet parameters

| Name       | Description                     | Default value     |
|------------|---------------------------------|-------------------|
| model      | 3D model to load                | models/3dview.asc |
| skin       | Background image                | assets/skin.gif   |
| shading    | Render with shading             | on                |
| wire       | Render as wireframe             | off               |
| pointorder | Invert order of points in faces | inverse           |
