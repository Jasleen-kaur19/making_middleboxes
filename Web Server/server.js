const http = require("http");
const express  = require("express");

const app1 = express();
const app2 = express();

app1.post("*", (req, res) => {
    res.status(200).send({
        data: "Response served from instance 1"
    })
})

app2.post("*", (req, res) => {
    res.status(200).send({
        data: "Response served from instance 2"
    })
})

const server1 = http.createServer(app1);
const server2 = http.createServer(app2);

server1.listen(3000, function() {
    console.log("First instance started on port 3000");
});

server2.listen(3001, function() {
    console.log("Second instance started on port 3001");
});