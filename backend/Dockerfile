# Базовый образ с Java 17 и Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Кэш зависимостей
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем всё остальное и собираем проект
COPY . .
RUN mvn clean package -DskipTests

# Финальный минимальный образ с JRE
FROM eclipse-temurin:17-jre-alpine

# Папка с jar
WORKDIR /app

# Копируем скомпилированный jar из предыдущего слоя
COPY --from=build /app/target/*.jar app.jar

# Открываем порт и запускаем
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
