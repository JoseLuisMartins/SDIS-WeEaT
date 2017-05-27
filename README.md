# SDIS-WeEaT

## Purpose of the Application 
Do you ever find yourself having to stay at school for lunch when all of your friends went home already? If so, do you eat all by yourself? It shouldn’t be like that. It won't be like that anymore with WeEaT you can quickly find someone or even a group of people whom you can go have lunch with. Sounds simple right? Because it is! Just download the app from the play store and login with your facebook account and we take care of the rest for you! The application makes it easy for college student to meet new peers and have a more active role within the student community.
The application will be focused on students, it’s main goal is to provide to students an easy way of finding colleagues to go get a meal with.

## Main Features 
* Find Nearby people;
* Allow filtering of people who can get in touch (i.e only facebook friends);
* Schedule time and places to get a meal;
* Form EatGroups;
* Rate people and restaurants;
* Provide Restaurant Menu;
* Send messages to other people.

## Web Services 
* Google Maps API
	* Location.
* Firebase API 
	* Login, notifications.
* Graph API
 * Login
		
## Our Web Service
* Places Registry
	* Add, Remove, Update Places to Eat.
	* Manage favorites of a user.
	* Requires authentication, and can be shared among all users.

* Group Management
	* Add, Remove, Update Groups.
	* Requires authentication.

* Restaurant Management
	* Add, Remove, Update Menus.
	* Requires authentication.
	* Rate the restaurant.

* Communication
	* Be able to send a message to everyone in a certain radius.
	* Rate different users

## Link Between Services
* Group Management will make sure to only associate elements in a group that are friends.
  Place registry requires authentication on the platform.
* Send messages using Graph API.
* Associate Menus with the restaurants, and their location using Google Maps API

## Target Platforms 
* Java standalone application for PC/Mac 
* Java Server Pages / Servlets 
* Java standalone application for Mobile Device (Android) 

## Additional Services and Improvements
* Fault tolerance	
* Security
* Scalability
* Consistency


# Run Instructions


## LoadBalancer:

    USAGE:
     	<loadBalancer_HTTPS_Port> <loadBalancer_SSLSOCKET_Port>

## Servidor:

    USAGE:
     	<locationString> <serverIp> <serverPort> <balancerIp> <balancerPort> <WebSocketPort> <backupPort> <path_to_pgsql_bin>

    

## Client

Install the apk.

## Developers :

* [David Azevedo](https://github.com/PeaceOff)
* [João Ferreira](https://github.com/joaocsf)
* [José Martins](https://github.com/JoseLuisMartins)
* [Marcelo Ferreira](https://github.com/mferreira96)
