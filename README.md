# Taller RMI 2025 – Sistemas Distribuidos

## 📌 Descripción
Proyecto académico que implementa un **sistema Cliente–Servidor usando Java RMI** para la gestión de una biblioteca.  
El servidor mantiene un repositorio de libros y expone operaciones remotas para **consultar, prestar y devolver ejemplares**.  
El cliente se conecta al servicio remoto y valida su funcionamiento con pruebas.

---

## 📂 Estructura del proyecto

TALLER-RMI-2025-SD
├── shared/   # Interfaces remotas y DTOs (LoanResult, QueryResult, ReturnResult)
├── server/   # Implementación del servicio, repositorio y ServerMain
├── client/   # Cliente que consume el servicio remoto
└── data/     # Archivo books.json con los libros iniciales

---

## ⚙️ Requisitos
- **Java 17+**
- **Maven 3.8+** (o usar el wrapper `./mvnw`)

---

## 🚀 Compilación
Desde la raíz del proyecto:

```bash
./mvnw clean package -DskipTests
o
mvn clean package -DskipTests
```

Esto genera:
	•	server/target/server-1.0.0-all.jar
	•	client/target/client-1.0.0.jar
	•	shared/target/shared-1.0.0.jar

⸻

▶️ Ejecución

1. Mismo computador (localhost)

Servidor:
```bash
java -Djava.rmi.server.hostname=127.0.0.1 \
     -cp server/target/server-1.0.0-all.jar \
     server.ServerMain
```

Cliente (otra terminal):

```bash
java -cp client/target/client-1.0.0.jar:shared/target/shared-1.0.0.jar \
     client.ClientMain 127.0.0.1
```

⸻

2. En la misma red (LAN)
	1.	En el servidor, obtener la IP local:

ipconfig getifaddr en0    # macOS/Linux

Ejemplo: 192.168.1.45

	2.	Servidor:

java -Djava.rmi.server.hostname=192.168.1.45 \
     -cp server/target/server-1.0.0-all.jar \
     server.ServerMain


	3.	Cliente (otro computador en la misma red):

java -cp client/target/client-1.0.0.jar:shared/target/shared-1.0.0.jar \
     client.ClientMain 192.168.1.45



⸻

📖 Ejemplo de salida del cliente

=== PRUEBAS OK ===
Query Clean Code:       QueryResult[exists=true, availableCount=3, message=OK]
Loan Clean Code:        LoanResult[ok=true, dueDate=2025-09-21, message=Préstamo OK]
Return Clean Code:      ReturnResult[ok=true, message=Devolución OK]
Query CLRS:             QueryResult[exists=true, availableCount=1, message=OK]

=== PRUEBAS DE ERRORES A PROPÓSITO ===
Query ISBN inexistente: QueryResult[exists=false, availableCount=0, message=No existe]
Loan por título inexistente: LoanResult[ok=false, dueDate=null, message=Título no existe]


⸻

👥 Autores
	•	Sebastián Vargas
	•	Alejandro Parrado Di Domenico
