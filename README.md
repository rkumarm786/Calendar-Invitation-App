# Calendar-Invitation-App
Application to create a  Calendar schedule and share with someone to schedule a meeting

## Technologies Used

```
    JAVA, Spring Boot Framework
    Database - Postgres
```

## Installation
- Add application.properties in `resources` folder under `main`
    ```
        main
            - resources
                - application.properties
    ```
- Propertiest to be added in properties file
    ```
        google.client.client-id=<Google Client Id>
        google.client.client-secret=<Google Client Secret>
        google.client.redirectUri=<Redirection url after login>
    
        spring.mvc.view.prefix=/WEB-INF/view/
        spring.mvc.view.suffix=.jsp
        
        spring.datasource.platform=postgres
        spring.datasource.url=jdbc:postgresql://localhost:5432/calendly
        spring.datasource.username=postgres
        spring.datasource.password=postgres
        
        //to automatically create tables from entites
        spring.jpa.hibernate.ddl-auto=update
    ```
    ```sh
    $ mvn clean install
    ```
    
    ```sh
    $ mvn spring-boot:run
    ```
- Now your application will be running on localhost:8080

# Api's
- `/save/event`
    ```
        {
        	"title": String,  
        	"description": String, 
        	"fromDate": Long,   //Timestamp of day at 12:00 am
        	"toDate": Long,     //Timestamp of day at 12:00 am
        	"live": boolean,
        	"customDateTime":[  //To customize your availability by day or date
        	    {
        	    	"type":int,     //can be 0(DAY) or 1(DATE)
        		    "intervals":[
        		    	{
        		    	"from":int,     //Minutes from 12:00 am ex. 540
        		    	"to":int        //Minutes from 12:00 am ex. 540
        		    	}
        	    	],
        		    "timeStamp":Long,   //Timestamp of day at 12:00 am
        		    "day":String        //Day in uppercase like "MONDAY"
        	    }
        	],
        	"interval":30   //Duration of event in mins ex. 60 min(1 hours meeting)
        }
    ```
    - In `custom date` if list is empty default availability will be from monday to sunday (9:00 am to 5:00 pm)
    - In customDateTime if type is 0 then you have to provide day or vice versa

- `/events`
    - this api will return all list of events created so far
    - Api Respone format
    ```
    [{
        "title": String,
        "description": String,
        "fromDate": Long,   //1580322600000
        "toDate": Long,     //1588185000000
        "url": String,          // 1e06b5c1-1d4e-4caf-9a58-7941ce5a1afa
        "interval": 30,
        "customDateTime": [
            {
                "type": "DAY",
                "intervals": [
                    {
                        "from": 100,
                        "to": 400
                    }
                ],
                "timeStamp": null,
                "day": "WEDNESDAY"
            },
            {
                "type": "DATE",
                "intervals": [
                    {
                        "from": 100,
                        "to": 400
                    }
                ],
                "timeStamp": 1586284200000,
                "day": null
            }
        ],
        "live": true,
        "active": true
    }]
    ```

## Improvement to be done

- Timezone handling will help to improve share url in multiple regions (on user interface)
- Cache can be implemented for fast processing of some apis
