# xCurator Importer

**Authors**: Nadav Babai | [3pc GmbH](https://www.3pc.de)

Importer for xCurator data sources.

### Data sources

- [BLM ExpoDB](https://expotest.bsz-bw.de/blm/cue/)
- [AP TIN](https://cloud.landesmuseum.de/nextcloud/index.php/s/ww8KMjTGHME8zzX?path=/01_Collection_Data/Allard%20Pierson/TIN/TIN&openfile=3749225&dir=undefined)
- [AP Sparql](https://lod.uba.uva.nl/)

### Build, Run Test

- Build: ```gradlew clean build```
- Run Unit Tests: ```gradlew clean test```
- Run Integration Tests: ```gradlew clean integrationTest```
- Run: ```gradlew bootRun``` (Docker-Compose Sevices vorher starten)

### 🔁 Run Import Job

To trigger an import job:

- Make sure to adjust config in `application-import.yml`
- Run with `importer` Spring profile.
- Application will be closed automatically at the end of the import

### Tech-Stack

- [Java 17](https://openjdk.java.net/projects/jdk/17/)
- [Apollo Federation](https://www.apollographql.com/docs/apollo-server/federation/introduction/)
- [MongoDb](https://www.mongodb.com/)
- Weitere Bibliotheken: Siehe ```build.gradle```

### More Info | Documentation | Papers

- [xCurator Data schema](https://app.diagrams.net/#G13n971FsvOwo4cIpKqM0p8qPCqnACBDsD)
- [ExpoDB Digital Catalog](https://expotest.bsz-bw.de/blm/digitaler-katalog/viewer/)
- [Konfiguration Expo-DB](https://expotest.bsz-bw.de/blm/cue/info)
