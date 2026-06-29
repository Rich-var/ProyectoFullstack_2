# Etapa 1: usa JDK 21 para compilar el proyecto Maven.
FROM eclipse-temurin:21-jdk AS build

# Define la carpeta de trabajo dentro del contenedor de compilación.
WORKDIR /app

# Copia el Maven Wrapper para usar la versión de Maven incluida en el proyecto.
COPY mvnw .

# Copia la configuración interna del Maven Wrapper.
COPY .mvn .mvn

# Copia el pom.xml para descargar dependencias antes de copiar todo el código.
COPY pom.xml .

# Da permisos de ejecución al Maven Wrapper y descarga dependencias para mejorar la caché de Docker.
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copia el código fuente de la aplicación.
COPY src src

# Compila la aplicación y genera el JAR sin ejecutar tests.
RUN ./mvnw -B clean package -DskipTests

# Etapa 2: usa solo JRE 21 para ejecutar la aplicación, dejando fuera herramientas de compilación.
FROM eclipse-temurin:21-jre

# Define la carpeta de trabajo dentro del contenedor final.
WORKDIR /app

# Copia el JAR generado en la etapa de compilación y lo renombra a app.jar.
COPY --from=build /app/target/*.jar app.jar

# Documenta el puerto interno usado por este microservicio.
EXPOSE 8083

# Ejecuta la aplicación Spring Boot cuando el contenedor inicia.
ENTRYPOINT ["java", "-jar", "app.jar"]