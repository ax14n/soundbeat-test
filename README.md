# ♯ 𝄞 ¿Buscas un reproductor propio? ¡SoundBeat es para ti! 𝄞 ♯

## ¿Qué es SoundBeat?

>[!NOTE]
>  SoundBeat fue construido esencialmente por mí como proyecto de final de grado, con el objetivo de presentarlo y cumplir los requisitos académicos. Sin embargo, más allá de eso, mi intención es moldearlo a mi gusto, creando una plataforma que se ajuste a mis necesidades personales. Cansado de la publicidad excesiva y los algoritmos invasivos de los servicios de streaming actuales, decidí crear mi propia alternativa.
> *¡Así nace SoundBeat!*

Podemos definir SoundBeat como una aplicación de streaming de música desarrollada en Kotlin con Jetpack Compose para Android. El sistema incluye un servidor propio que transmite canciones en tiempo real mediante HLS (HTTP Live Streaming), y una app móvil que permite a los usuarios escuchar música sin interrupciones.

También ofrece funciones como la creación de playlists personalizadas, combinando canciones locales y remotas. Es un proyecto completo que integra trabajo con redes, multimedia, bases de datos locales (Room) y una interfaz moderna y reactiva, aprovechando el stack actual de desarrollo en Android.

## Diagrama de pantallas de SoundBeat!

>[!WARNING]
>  Las visuales no son definitivas y pueden cambiar. Los cambios no serán drásticos, pero puede haber cambios mínimos en cuanto paletas de colores o la ubicación de los controles de reproducción dentro de la pantalla. 

![Captura de pantalla_2025-05-15_22-53-57](https://github.com/user-attachments/assets/272abdca-1eb4-4cc0-b6f3-c31a24e89284)

## FAQ

- **¿Dónde consigo el servidor personal para mi aplicación SoundBeat?**

Puedes clonar el repositorio que contiene el servidor de SoundBeat. Tienes dos opciones: descargar el archivo comprimido desde GitHub o usar git desde tu terminal ejecutando el siguiente comando:
```BASH
git clone https://github.com/ax14n/sound-beat-server.git
```
Si quieres más información sobre el servidor, puedes visitar su [repositorio dedicado](https://github.com/ax14n/sound-beat-server). Allí encontrarás todos los detalles necesarios sobre su funcionamiento e implementación. Aunque mi proyecto de final de grado no requería una arquitectura cliente-servidor decidí ir más allá. 

> "You have to let it all go, Neo. Fear, doubt, and disbelief. Free your mind." — ("The Matrix" (1999))

Usar servicios de terceros puede ser cómodo, pero todo lo que subas allí depende de una infraestructura que no controlas. Y eso, personalmente, no me convence, y por ende, terminé haciendo un servidor. Quizás demasiado simple y mejorable, pero completamente propio.

- **¿Hay planes de continuar con el desarrollo de SoundBeat?**

Sí, SoundBeat continuará desarrollandose a futuro. Aunque sea un proyecto surgido en base a mi final de grado, no deja de ser un proyecto personal en el cuál estoy interesado continuar. 

A diferencia de otros reproductores de música del mercado, quiero ofrecer un control total sobre la disponibilidad, las funcionabilidades y aspectos visuales de la misma. No soy un desarrollador experimentado, pero sí tengo la suficiente determinación para encerrarme en una habitación y desintoxicarme de las abusivas aplicaciones de terceros que invaden nuestros gloriosos sistemas. Por si no ha quedado claro: SoundBeat quiere ofrecer libertad. ¿Por qué pagar por servicios donde solo quiero escuchar música y te llenan a publicidad? ¡¿EH?! ¿Han eliminado mi canción favorita? No más.
