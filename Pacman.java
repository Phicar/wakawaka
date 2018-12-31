import java.util.*;
import java.awt.*;
import java.awt.Color.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
public class Pacman extends JFrame implements WindowListener,KeyListener,ActionListener{
public static boolean salir = false;
public static boolean jugar = false;
public static int nojugarTiempo = 0;
public static int animacion = 0;
public static int screenX=500,screenY=500;
public static int screenMarX = 50,screenMarY=50;
public static int vivos = 0;
public static boolean pausa = false;
public static int pausaTiempo = 0;
public static Vector<pac> Pacmans;
public static Vector<ghost> Ghosts; 
public static int pell[][];
public static void main(String args[]) throws Exception{
pell = new int[screenX/15][screenY/15];
Pacman pc = new Pacman();
}
public Pacman() throws Exception{
super("Pacman");
Container cont = this.getContentPane();
addKeyListener(this);
addWindowListener(this);
cont = getContentPane();
cont.setBackground(new Color(0,0,0));
setSize(screenX+2*screenMarX,2*screenMarY+screenY);
setVisible(true);
Crear();
boolean perdio = false;
while(!salir){
if(jugar){
nojugarTiempo=0;
Jugar();
Crear();
}else{
nojugarTiempo++;
}
repaint();
try{
Thread.sleep(200);
}catch(Exception exep){
System.err.println("Ahhhh "+exep);
}
}
}
public void Crear(){
for(int n = 0;n<pell.length;n++){
for(int m = 0;m<pell[n].length;m++)
pell[n][m]=((int)Math.ceil(100.0*Math.random()))%50;
}
Ghosts = new Vector<ghost>();
Color colores[] = {Color.red,Color.blue,Color.orange,Color.pink};
for(int n = 0;n<4;n++){
int gx = (int)Math.round(((double)screenX)*Math.random());
int gy = (int)Math.round(((double)screenY)*Math.random());
Ghosts.add(new ghost(gx,gy,10*n,colores[n]));
}
Pacmans = new Vector<pac>();
Pacmans.add(new pac(screenX/2,screenY/2,Color.yellow,15,0));
vivos++;
}
public void Jugar(){
while(vivos>0){
if(!pausa){
Mover();
ComioPell();
ComioGho();
}else
pausaTiempo++;
repaint();
try{
Thread.sleep((pausa)?200:120);
}catch(Exception ex){
System.err.println("aa "+ex);
}
}
jugar = false;
animacion = 30;
while(animacion>0){
repaint();
try{
Thread.sleep(200);
}catch(Exception er){}
animacion--;
}
}
public void ComioGho(){
for(int n = 0;n<Pacmans.size();n++){
for(int m = 0;m<Ghosts.size();m++){
if(Math.abs(Pacmans.get(n).x-Ghosts.get(m).x)<15.0 &&
Math.abs(Pacmans.get(n).y-Ghosts.get(m).y)<15.0 ){
System.out.println("PAAAAAAAACMAN con "+m);
if(Pacmans.get(n).modoRambo){
Pacmans.get(n).score+=100;
Color col = Ghosts.get(m).color;
int tipOld = Ghosts.get(m).type;
int gx =(int)Math.round(((double)screenX)*Math.random());
int gy =(int)Math.round(((double)screenY)*Math.random());
Ghosts.set(m,new ghost(gx,gy,tipOld,col));
}else{
Pacmans.get(n).vivo=false;
vivos--;
}
}
}
}
}
public void ComioPell(){
for(int n = 0;n<pell.length;n++){
for(int m = 0;m<pell[n].length;m++){
if(pell[n][m]>5)continue;
boolean si = false;
for(int k=0;k<Pacmans.size();k++)
if((int)Math.round(Pacmans.get(k).x/15.0)==n && (int)Math.round(Pacmans.get(k).y/15.0)==m){
if(pell[n][m]==0){
Pacmans.get(k).score+=50;
Pacmans.get(k).modoRambo = true;
Pacmans.get(k).tiempoRambo = 40;//depends on level
si=true;
}else
Pacmans.get(k).score+=10;
si=true;
}
if(si)pell[n][m]=10;
}
}
}
public void Mover(){
//mover fantasmas
for(int n = 0;n<Ghosts.size();n++)
Ghosts.get(n).mover(screenX,screenY,Pacmans.get(0).modoRambo);
//mover pacman
for(int n=0;n<Pacmans.size();n++){
Pacmans.get(n).mover(screenX,screenY);
if(Pacmans.get(n).tiempoRambo>0)Pacmans.get(n).tiempoRambo--;
if(Pacmans.get(n).tiempoRambo==0 && Pacmans.get(n).modoRambo)Pacmans.get(n).modoRambo=false;
}
}
public void paint(Graphics g){
super.paint(g);
update(g);
}
public void update(Graphics g){
Graphics off;
Image offscreen = createImage(screenX+2*screenMarX,2*screenMarY+screenY);
off =offscreen.getGraphics();
off.setColor(Color.black);
off.fillRect(0,0,2*screenMarX+screenX,2*screenMarY+screenY);
off.setColor(Color.white);
off.drawRect(screenMarX,screenMarY,screenX,screenY);
off.setColor(vivos>0?Color.white:Color.red);
off.drawString(vivos>0?"Score:"+Pacmans.get(0).score:"GAME OVER",10,40);
//pintar pelle
for(int n = 0;n<pell.length;n++)
for(int m = 0;m<pell[n].length;m++)
if(pell[n][m]<5){
off.setColor(Color.white);
if(pell[n][m]==0)
off.fillOval(screenMarX+15*n+1,screenMarY+15*m+1,7,7);
else
off.fillOval(screenMarX+15*n+5,screenMarY+15*m+5,5,5);
}
//pintar pacmans
for(int n = 0;n<Pacmans.size();n++){
off.setColor(Pacmans.get(n).color);
int st=Pacmans.get(n).dir%2==1?135+90*((Pacmans.get(n).dir+2)%4):135+90*Pacmans.get(n).dir;
if(animacion>0)
off.fillArc(screenMarX+(int)Pacmans.get(n).x,
screenMarY+(int)Pacmans.get(n).y,
Pacmans.get(n).tam,Pacmans.get(n).tam,st,(Pacmans.get(n).tiempo<2?-270:-360)+(animacion*315/30));
else{
off.fillArc(screenMarX+(int)Pacmans.get(n).x,
screenMarY+(int)Pacmans.get(n).y,
Pacmans.get(n).tam,Pacmans.get(n).tam,st,Pacmans.get(n).tiempo<2?-270:-360);
}
}
//pintar ghosts
for(int n = 0;n<Ghosts.size();n++){
off.setColor(Ghosts.get(n).color);
off.fillRect(screenMarX+(int)Ghosts.get(n).x,
screenMarY+(int)Ghosts.get(n).y,Ghosts.get(n).tam,Ghosts.get(n).tam);
if(Pacmans.get(0).modoRambo){
off.setColor(new Color(0,0,126));
off.fillRect(screenMarX+(int)Ghosts.get(n).x+1,
screenMarY+(int)Ghosts.get(n).y+1,Ghosts.get(n).tam-2,Ghosts.get(n).tam-2);
}
}
if(pausa){
off.setColor(Color.red);
off.drawString("PAUSA",screenMarX+screenX/2,screenMarY+(pausaTiempo*6)%screenY);

}
if(!jugar){
off.setColor(Color.red);
off.drawString("WAKA WAKA WAKA",screenMarX+(nojugarTiempo*6)%screenX,screenMarY+(nojugarTiempo*6)%screenY);
}
g.drawImage(offscreen,0,0,this);
}
public void keyTyped(KeyEvent e){}
public void keyPressed(KeyEvent e){
int t = (int)e.getKeyCode();
if(t>=37 && t<=40)Pacmans.get(0).dir = t%37;
else if(t==32 && jugar)pausa = !pausa;
else if(t==32 && !jugar)jugar = true;
}
public void keyReleased(KeyEvent e){}
public void windowActivated(WindowEvent e){}
public void windowClosed(WindowEvent e){}
public void windowDeactivated(WindowEvent e){}
public void windowDeiconified(WindowEvent e){}
public void windowIconified(WindowEvent e){}
public void windowOpened(WindowEvent e){}
public void windowClosing(WindowEvent e){}
public void actionPerformed(ActionEvent e){}
public static double dist(double a,double b,int c){//x1,x2,lengthofScreen
double mi = Math.min(a,b);
double ma = Math.max(a,b);
return Math.min(ma-mi,mi+c-ma);
}
public static int distDir(double a,double b,int c){
if(a<b)
return b-a<a+c-b?1:0;
return a-b<b+c-a?0:1;
}
}
class pell{
double x, y;
}
class ghost{
public double x,y;
public double dv[][] ={{-1.0,0.0},{0.0,-1.0},{1.0,0.0},{0.0,1.0}};
public  int dir = 0;
public int type;
public int tam=10;
public double v=1.0;
public Color color;
public boolean anest;
public ghost(int x,int y,int type,Color color){
this.color = color;
this.x =x;
this.y =y;
this.tam = tam;
this.type = type;
}
public void mover(int sX,int sY,boolean modoRambo){
int ra = -1;
do
ra = (int)Math.round(4.0*Math.random());
while(ra==0 || ra>=4);
if((int)Math.round(100.0*Math.random())%6==0)
dir = (ra+dir)%4;
boolean estamosRambo = Pacman.Pacmans.get(0).modoRambo;
if(((int)Math.round(((double)type)*Math.random()))%5==0 || estamosRambo){
double minD = Double.MAX_VALUE;
int pacTar = 0;
for(int n = 0;n<Pacman.Pacmans.size();n++){
double disP = Pacman.dist(Pacman.Pacmans.get(n).x,x,Pacman.screenX)+
Pacman.dist(Pacman.Pacmans.get(n).y,y,Pacman.screenY);
minD = Math.min(minD,disP);
if(minD==disP)pacTar=n;
}

double distX = Pacman.dist(x,Pacman.Pacmans.get(pacTar).x,Pacman.screenX);
double distY = Pacman.dist(y,Pacman.Pacmans.get(pacTar).y,Pacman.screenY);
int dirX = Pacman.distDir(x,Pacman.Pacmans.get(pacTar).x,Pacman.screenX);
int dirY = Pacman.distDir(y,Pacman.Pacmans.get(pacTar).y,Pacman.screenY);
if(estamosRambo){
dirX =1-dirX;
dirY = 1-dirY;
}
if(distX>distY)
dir = 2*dirX;
else
dir = 2*dirY+1;
}

//if(type==1)
//System.out.println(color+" "+type+": "+dir);
x+=dv[dir][0]*(tam-(modoRambo?5:0));
y+=dv[dir][1]*(tam-(modoRambo?5:0));
if(x>sX)x-=sX;
if(y>sY)y-=sY;
if(x<0)x+=sX;
if(y<0)y+=sY;
}
}
class pac{
public boolean modoRambo = false;
public static int tiempoRambo = 0;
public boolean vivo = true;
public int score = 0;
public double x,y;
public double dv[][] ={{-1.0,0.0},{0.0,-1.0},{1.0,0.0},{0.0,1.0}};
public  int dir = 0;
public int tam=10;
public double v=1.0;
public Color color;
public int tiempo = 0;
public pac(double x,double y,Color color,int tam,int tiempo){
this.color = color;
this.tiempo = tiempo;
this.x = x;
this.y = y;
this.tam = tam;
}
public void mover(int sX,int sY){
tiempo=(tiempo+1)%4;
x+=dv[dir][0]*tam;
if(x<0)x+=sX;
if(x>sX)x-=sX;
y+=dv[dir][1]*tam;
if(y>sY)y-=sY;
if(y<0)y+=sY;
}
}
