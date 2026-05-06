const express = require("express");
const passwordManager = require("./password-manager/password-manager.service");
const notesService = require("./notes/notes.service");
const authService = require("./auth/auth.service");
const Logger = require("log-byte");

const app = express();
app.use(express.json());
app.use((req, res, next) => {
  const { ip, method, body, url } = req;
  Logger.info(`ip : ${ip}`);
  Logger.info(`method : ${method}`);
  Logger.info(`body : ${body}`);
  Logger.info(`url : ${url}`);
  next();
});
app.get("", (_, res) => {
  res.status(200).send("welcome in password-manager-server");
});

app.post("/password/save", async (req, res) => {
  console.log(req.body)
  const data = await new passwordManager().save(req.body);
  res.status(data.statusCode).send(data);
});

app.get("/password/get", async (_, res) => {
  const data = await new passwordManager().getAll();
  res.status(data.statusCode).send(data);
});

app.post("/password/update", async (req, res) => {
  const data = await new passwordManager().update(req.body);
  res.status(data.statusCode).send(data);
});

app.post("/notes/save", async (req, res) => {
  const data = await new notesService().save(req.body);
  res.status(data.statusCode).send(data);
});

app.get("/notes/get", async (_, res) => {
  const data = await new notesService().getAll();
  res.status(data.statusCode).send(data);
});

app.post("/notes/update", async (req, res) => {
  const data = await new notesService().update(req.body);
  res.status(data.statusCode).send(data);
});

app.post("/auth/save", async (req, res) => {
  const data = await new authService().save(req.body);
  res.status(data.statusCode).send(data);
});

app.post("/auth/get", async (req, res) => {
  const data = await new authService().get(req.body);
  res.status(data.statusCode).send(data);
});

app.listen(3000, "0.0.0.0", (e) => {
  e ? Logger.error(e.message) : Logger.info("http://0.0.0.0:3000");
});
