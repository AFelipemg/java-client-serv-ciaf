
import express from 'express' // Importa el módulo Express para crear el servidor web
import logger from 'morgan' // Importa el módulo Morgan para el registro de solicitudes HTTP
import dotenv from 'dotenv' // Importa el módulo dotenv para cargar variables de entorno desde un archivo .env
import { createClient } from '@libsql/client' // Importa la función createClient del módulo libsql/client para crear un cliente de base de datos

import { Server } from 'socket.io' // Importa la clase Server del módulo socket.io para configurar el servidor de WebSockets
import { createServer } from 'node:http' // Importa la función createServer del módulo http de Node.js para crear un servidor HTTP

dotenv.config() // Carga las variables de entorno desde el archivo .env

const port = process.env.PORT ?? 3000 // Obtiene el número de puerto del archivo .env o utiliza el puerto 3000 por defecto


const app = express() // Crea una instancia de la aplicación Express

const server = createServer(app) // Crea un servidor HTTP utilizando la aplicación Express
const io = new Server(server, { // Configura el servidor de WebSockets utilizando la instancia del servidor HTTP
    connectionStateRecovery: {} // Opciones adicionales para el servidor de WebSockets
})
// Configura Express para servir archivos estáticos desde la carpeta 'client'
app.use(express.static('client'))


const db = createClient({ // Crea un cliente de base de datos utilizando la función createClient
    url:"libsql://tender-psycho-man-afelipemg.turso.io", // URL de la base de datos
    authToken: process.env.DB_TOKEN // Token de autenticación para acceder a la base de datos
})
// Ejecuta una consulta SQL para crear la tabla "messages" si no existe
await db.execute(`
    CREATE TABLE IF NOT EXISTS messages (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        content TEXT,
        user TEXT,
        timestamp DATE
    )
`)

io.on('connection', async (socket) => { // Maneja el evento de conexión de un cliente al servidor de WebSockets

    console.log('an user has connected!') // Registra un mensaje cuando un usuario se conecta al servidor

    socket.on('disconnect', () => { // Maneja el evento de desconexión de un cliente
        console.log('an user has disconnected') // Registra un mensaje cuando un usuario se desconecta del servidor
    })

    socket.on('chat message', async (msg)=>{ // Maneja el evento de recepción de un mensaje de chat
        const now = new Date();
        const timestamp = now.toLocaleString();
        let result // Variable para almacenar el resultado de la ejecución de la consulta SQL
        const username = socket.handshake.auth.username ?? 'anonymous' // Obtiene el nombre de usuario del cliente o utiliza 'anonymous' por defecto
        console.log('username:'+ username)
        try {

            result = await db.execute({ // Ejecuta una consulta SQL para insertar el mensaje en la base de datos
                sql: 'INSERT INTO messages (content, user, timestamp) VALUES (:msg, :username, :timestamp)',
                args: { msg, username, timestamp }
            })
        }   catch (e) {
            console.error(e) // Registra un error si ocurre un problema durante la ejecución de la consulta SQL
            return
        }
        console.log('message: ' + msg) // Registra el mensaje recibido en la consola del servidor

        io.emit('chat message', msg, result.lastInsertRowid.toString(), username, timestamp ) // Emite el mensaje a todos los clientes conectados





    })

    if(!socket.recovered) { // Verifica si el cliente no ha recuperado los mensajes anteriores después de una desconexión
        try{
            const results = await db.execute({ // Ejecuta una consulta SQL para obtener los mensajes anteriores de la base de datos
                sql: 'SELECT id, content, user FROM messages WHERE id > ?',
                args: [socket.handshake.auth.serverOffset ?? 0]
            })

            results.rows.forEach(row => { // Recorre los resultados de la consulta y emite los mensajes a través de WebSockets
                socket.emit('chat message', row.content, row.id.toString(), row.user, row.timestamp)
            })
        } catch (e) {
            console.error(e) // Registra un error si ocurre un problema durante la ejecución de la consulta SQL
        }
    }
})



app.use(logger('dev')) // Utiliza el middleware de registro de solicitudes HTTP proporcionado por Morgan

app.get('/', (req, res) =>{ // Define una ruta para manejar las solicitudes GET a la raíz del servidor
    res.sendFile(process.cwd() + '/client/index.html') // Envía el archivo HTML de la página principal al cliente
})

server.listen(port, ()=>{ // Inicia el servidor y escucha las conexiones en el puerto especificado
    console.log(`server running on port ${port}`) // Registra un mensaje cuando el servidor se inicia correctamente
})
