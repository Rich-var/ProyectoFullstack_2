# Etapa 1: usa JDK 21 para compilar el proyecto Maven.
FROM eclipse-temurin:21-jdk AS build

# Define la carpeta de trabajo dentro del contenedor de compilacion.
WORKDIR /app

# Copia el Maven Wrapper para usar la version de Maven incluida en el proyecto.
COPY mvnw .
COPY .mvn .mvn

# Copia el pom.xml para descargar dependencias antes de copiar todo el codigo (mejor cache de Docker).
COPY pom.xml .

# Da permisos de ejecucion al Maven Wrapper y descarga dependencias.
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copia el codigo fuente de la aplicacion.
COPY src src

# Compila la aplicacion y genera el JAR sin ejecutar tests (los tests se ejecutan aparte en CI/local).
RUN ./mvnw -B clean package -DskipTests

# Etapa 2: usa solo JRE 21 para ejecutar la aplicacion, sin herramientas de compilacion.
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia el JAR generado en la etapa de compilacion y lo renombra a app.jar.
COPY --from=build /app/target/*.jar app.jar

# Documenta el puerto interno usado por este microservicio.
EXPOSE 8089

# Ejecuta la aplicacion Spring Boot cuando el contenedor inicia.
ENTRYPOINT ["java", "-jar", "app.jar"]
