# Mars Live

Mars Live is an interactive game that transports players to mars. It was created during NASA [Space Apps Hackathon](https://2016.spaceappschallenge.org/). Please bear in mind that we had limited time so the game might contain bugs and/or unfinished features. Though the project was created during the hackathon, our team will try and maintain the game in our free time.

### The Game
The idea is that a player will be placed on a location on mars according to his/her earth coordinates so that if two players are close on earth, they will be close on mars too. Each player is placed in a colony according to his location, and is given a set of tasks that he/she needs to complete in order to maintain their colony. Users will be able to browse the map, analize data, and learn new things about the red planet as they play.

![Mars Live](http://i.imgur.com/wi0pwFl.png?1)

### The Map
The game is played on a map of the mars. There are 3 different maps in the game, an elevation map, an infrared map and a map of visible surface. All 3 of them are fetched from google's servers at [google.com/mars](http://google.com/mars). We do not have a written permision to use the maps, but we believe that our use falls under [fair use](https://www.wikiwand.com/en/Fair_use). If anybody request the removal of any of the maps at any time we will be happy to comply and try to find alternatives.

![Imgur](http://i.imgur.com/yohInr3.jpg)
### Coordinate Mapping

We use players earth longitude and latitude to place him/her on mars. To picture it visually imagine if we put mars inside the earth, and connected any point on the surface of the earth to the center of the planet, then took the place of intersection of that line and mars as our martian position.
![Mars live](http://i.imgur.com/wUChl9t.png?1)

### Mars locations

We have included more than 1500 craters, mountains and other geographical sights in the application. Players can use our search functionality to find locations of those sigts and explore them on the map of their choice.

### Mars data

By longpressing at any location on the map, user can find information about that location such as approximate elevation (in meters), predicted temperature, pressure, the speed of horisontal wind and more. We have used several websited to fetch this information:
* [Mars Color Map Jmars](http://jmars.mars.asu.edu/maps/?layer=MOLA_Color) to find elevation at any point
* [Mars Climate Database](http://www-mars.lmd.jussieu.fr/mcd_python/) to find current predicted temperature, pressure and the velocity of horizontal wind.

![Long press](http://i.imgur.com/g7Nnkt5.png?1)

### Colonies

A player is assigned to a colony according to his mapped location on mars such that players within 5km are assigned to the same colony. A colony can contain unlimited number of players. If a player is not located in the territory of any of the existing colonies, a new one is created. Users can see all of the existing colonies on the map in real time. Each colony has a live set of tasks that need to be taken care off, such as doing research about the red planet (finding the elevation of some point on the map, or predicting weather elements) and tasks about maintenance, such as cleaning the solar panels.​

![Colony](http://i.imgur.com/RI2QlFt.png?1)

### Chat

In order to add a more social feel to the game and make players feel on the same planet, we have implemented a simple chat interface that allows players to communicate with each other even though they are on the different parts of the planet.

### Software We Used

As writing the whole application from scratch would be too time consuming we have used several open source plugins to complete our task

* [Andorid TileView](/moagrius/TileView) for displaying maps
* [Picasso](http://square.github.io/picasso/) for loading images
* [Floating search view](/arimorty/floatingsearchview) for displaying search results
* [Rajawali](/Rajawali/Rajawali) for rendering 3D content
* [Bottomsheet](/Flipboard/bottomsheet) for several UI components
* [Gson](/google/gson) for parsing server data
* [OKhttp](/square/okhttp) loading http data
