const express = require("express");
const passwordManager = require("./password-manager/password-manager.service");
const notesService = require("./notes/notes.service");
const authService = require("./auth/auth.service");

const app = express();
app.use(express.json());

app.get("", (_, res) => {
  res.status(200).send("welcome in password-manager-server");
});

app.post("/password/save", async (req, res) => {
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
  console.log(data, 0)
  res.status(data.statusCode).send(data);
});

app.listen(3000, "0.0.0.0", (e) => {
  e ? console.error(e.message) : console.log("http://0.0.0.0:3000");
});
