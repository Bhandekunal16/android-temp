const express = require("express");
const passwordManager = require("./password-manager/password-manager.service");
const notesService = require("./notes/notes.service");
const authService = require("./auth/auth.service");
const Logger = require("log-byte");

const app = express();
app.use(express.json());
app.use((req, res, next) => {
  res.on("finish", () => {
    const { ip, method, body, url } = req;
    const { statusCode } = res;

    if (statusCode == 200) {
      Logger.success(`ip : ${ip}`);
      Logger.success(`method : ${method}`);
      Logger.success(`body : ${body}`);
      Logger.success(`url : ${url}`);
    } else if (statusCode >= 400 && statusCode < 500) {
      Logger.warn(`ip : ${ip}`);
      Logger.warn(`method : ${method}`);
      Logger.warn(`body : ${body}`);
      Logger.warn(`url : ${url}`);
    } else {
      Logger.error(`ip : ${ip}`);
      Logger.error(`method : ${method}`);
      Logger.error(`body : ${body}`);
      Logger.error(`url : ${url}`);
    }
  });

  next();
});
app.get("", (_, res) => {
  res.status(200).send("welcome in password-manager-server");
});

app.post("/password/save", async (req, res) => {
  const data = await new passwordManager().save(req.body);
  res.status(data.statusCode).send(data);
});

app.get("/password/get", async (req, res) => {
  const { id } = req.query;

  if (!id)
    return res.status(400).send({
      status: false,
      message: "_id is required",
      statusCode: 400,
    });

  const data = await new passwordManager().getById(id);
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

app.post("/password/delete", async (req, res) => {
  const data = await new passwordManager().delete(req.body);
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
