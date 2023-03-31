DS - PR2

Player.java
- Se actualiza la clase Player haciendo que implemente la interfaz Comparable según el criterio de ordenación de su id.
- Se añade el atributo ratings, que es la lista de valoraciones publicadas por el jugador.
- Se añade el atributo level, que es el nivel del jugador en función del número de ratings que haya publicado. Se actualiza cada vez que publique un rating.
- Se añade el atributo numRatings, que contiene el número de valoraciones publicadas por el jugador. Se actualiza cada vez que publique un rating.
- Se añaden los atributos numFollowers y numFollowings para poder acceder en tiempo constante al número de seguidores y seguidos del jugador.
- Se añade posts, que contiene los posts publicados por el jugador.
- Se añaden los métodos necesarios para el acceso y manipulación de los atributos mencionados.

OrganizingEntity.java
- Se modifica la definición de la clase para que implemente la interfaz Comparable, comparando su id.
- Se modifica el tipo de la id a String.
- Se añade el atributo numAttendees para poder conocer en tiempo constante el número de asistentes que una entidad ha llevado al club a través de los eventos que ha organizado.
- Se modifican y añaden los métodos necesarios para recuperar y modificar los atributos mencionados.

File.java
- Se actualiza la clase File haciendo que implemente la interfaz Comparable según el criterio de ordenación de startDate y capacity.

SportEvent.java
- Se modifica la implementación de Comparable para que compare según la id del evento, que es el criterio de ordenación del TAD que almacena los eventos sportEvents.
- Se añade la cola con prioridad para los Player suplentes con criterio de ordenación según el valor del atributo level.
- Se añade el atributo attendees de tipo Dictionary e instanciado como HashTable que contiene los asistentes al evento.
- Se añade el método addAttendee para añadir asistentes al evento.
- Se añade la lista de trabajadores asociados al evento.
- Se modifican o añaden los métodos necesarios para acceder, modificar o añadir elementos a los atributos de los eventos.

Role.java
- Se crea la clase Role que contiene los atributos y métodos necesarios para almacenar y manipular su información.
- Los roles contienen y mantienen actualizada una lista con los trabajadores con ese determinado rol.

Worker.java
- Se crea la clase Worker que contiene los atributos y métodos necesarios para almacenar y manipular su información.

Attender.java
- Se crea la clase Attender que contiene los atributos y métodos necesarios para almacenar y manipular su información.

Post.java
- Con respecto a los posts publicados por los jugadores, se ha generalizado la clase abstracta Post con los atributos y métodos comunes a los distintos tipos de Post.
- Se crean dos clases que heredan de Post: RatingPost y SignUpPost. Implementan el método message().

SportEvents4ClubImpl.java
- Se modifica el tipo del atributo de players a la interfaz Dictionary, y se inicializa con el TAD DictionaryAVLImpl.
- Se modifica el tipo del atributo organizingEntities a la interfaz Dictionary, y se inicializa con el TAD HashTable.
- Se modifica la inicialización del atributo files al TAD PriorityQueue, conservando el tipo de su interfaz Queue.
- Se inicializa sportEvents a un diccionario implementado en árbol AVL, DictionaryAVLImpl.
- Se modifica la inicialización del vector bestSportEvents para que contenga tan solo los 10 eventos deportivos con mayor puntuación.
- Se añade el array de roles, junto con un atributo para el número de Role almacenados en cada momento.
- Se añade la tabla de dispersión de workers, junto con la implementación de sus métodos asociados.
- Se crean las clases Role y Worker, con sus atributos y estructuras asociadas.
- Se hacen todas las modificaciones necesarias para adaptar los métodos de la PR1 a las estructuras que han cambiado.
- Se modifica el método signUpEvent para que agregue al jugador en la cola de substitutos del evento en caso de que se hubiera llenado la cola de inscripciones.
- Se crea el método accesorio updateMostActivePlayer() que actualiza el apuntador al jugador más activo cada vez que se ejecuta signUpEvent().
- Se modifica el método addRating() para que añada la valoración en el jugador correspondiente, y para que añada su RatingPost.
- En la implementación del método addWorker() se debe actualizar el listado de roles en Worker y el listado de trabajadores en Role.
- El método addAttender(), además de añadir el asistente al evento, debe incrementar el contador de asistentes de la entidad, actualizar el vector con las 5 entidades que más asistentes han traído y actualizar el mejor evento por asistentes.
- El método addFollower() trata de obtener los vértices de la red social correspondientes a los jugadores, si no existen, los crea. Seguidamente, crea la arista con origen en player y destino en playerFollower. Finalmente, actualiza el contador de followers en player y followings en playerFollower.
- El método getFollowers() recupera el vértice correspondiente al jugador y, seguidamente, obtiene un iterador con las aristas que salen de él hacia los followers. Con un recorrido por el iterador, se van recuperando los followers en una lista y se devuelve el iterador de followers.
- El método getFollowings() funciona de la misma manera que getFollowers(), pero ahora no se recuperan las aristas con origen en el vértice, sino las que tienen destino al vértice.
- El método recommendations() hace un doble recorrido: un primer recorrido para obtener los followers del jugador, y un segundo para obtener los followers de los followers, que serán las recomendaciones. Hay que tener en cuenta que los que ya son followers no hay que recomendarlos, y que no puede haber recomendaciones repetidas.
- Para el método recommendations() se han creado dos métodos accesorios: isFollowing() para determinar si un jugador sigue a otro, e isIn para determinar si un jugador ya se encontraba en la lista de recomendaciones.
- El método getPosts() hace un primer recorrido por los jugadores a los que sigue y un segundo recorrido para obtener sus posts.

SportEvents4ClubPR2TestPlusBis.java
- En updateFileTestBis() se prueba que se lance la excepción NoFilesException cuando se intenta actualizar una ficha sin que haya ninguna.
- En signUpEventTestBis() se prueba que se lancen las excepciones PlayerNotFoundException y SportEventNotFoundException cuando no existan el jugador y el evento, respectivamente. También se crea un evento de prueba para probar que la cola de jugadores substitutos funcione correctamente al añadir jugadores por encima de la capacidad máxima.
- En getSportEventsByPlayerTestBis() se prueba que se lance la excepción NoSportEventsException tanto si no existe el jugador, como si no tiene ningún evento registrado.
- En addRatingTestBis() se prueba el lanzamiento de las excepciones.
- En best5OrganizingEntitiesTestBis() se comprueba el correcto criterio de ordenación de las entidades según asistentes que han traído al club.
- En bestSportEventByAttendersTestBis() se comprueba que se referencie correctamente al evento con más asistentes cuando se produce un cambio de mejor evento según asistentes.
- En followersFollowingsTestBis() se amplían las pruebas de los métodos addFollower(), getFollowers() y getFollowings() al añadir un follower a un jugador.
- En recommendationTestBis() se comprueba el correcto funcionamiento de las recomendaciones al añadir una relación nueva de seguimiento (follower de follower).
- En getPostsTestBis() se amplían las pruebas añadiendo nuevos posts en un jugador y comprobando que se recomienden en otro seguidor que lo sigue.
