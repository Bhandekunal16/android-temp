const databaseService = require("../core/database.service");
const validatorService = require("../core/datavalidate.service");

class notesService {
  #db;
  #client;
  #serviceName;
  #validator;
  #notesObjKeys;

  constructor() {
    this.#db = new databaseService();
    this.#validator = new validatorService();
    this.#client = this.#db.client;
    this.#serviceName = "notes";
    this.#notesObjKeys = ["id", "title", "content"];
  }

  #database = async () => {
    return this.#db.getMongoDb(process.env.MONGO_DB_NAME);
  };

  #collectionJoin = async (name) => {
    return (await this.#database()).collection(name);
  };

  notes = async () => {
    return await this.#collectionJoin(this.#serviceName);
  };

  async #validateNotesObj(obj) {
    const checkMissing = await this.#validator.checkMissing(
      Object.keys(obj),
      this.#notesObjKeys,
      Object.values(obj),
    );
    if (!checkMissing) return checkMissing;
    return this.#validator.checkStrings(obj);
  }

  async save(data) {
    try {
      const validateNotes = await this.#validateNotesObj(data);
      if (!validateNotes) {
        return {
          status: false,
          statusCode: 400,
          message: "bad request, please check request data!",
        };
      }

      const presentInDbCheck = await this.get(data);
      if (!presentInDbCheck.status) {
        return {
          status: false,
          statusCode: 409,
          message: "already present in database",
        };
      }

      const NotesCollection = await this.notes();
      await NotesCollection.insertOne(data);
      return {
        status: true,
        statusCode: 200,
        message: "notes save successfully!",
      };
    } catch (e) {
      const { message } = e;
      return { message, status: false, statusCode: 500 };
    }
  }

  async getAll() {
    try {
      const NotesCollection = await this.notes();
      const data = await NotesCollection.find().sort({ _id: -1 }).toArray();
      return data.length
        ? { data, status: true, statusCode: 200, message: "data found!" }
        : {
            data: [],
            status: false,
            statusCode: 404,
            message: "data Not found!",
          };
    } catch (e) {
      const { message } = e;
      return { message, status: false, statusCode: 500 };
    }
  }

  async update(data) {
    try {
      const { id, ...input } = data;
      const passwordCollection = await this.notes();
      const updated = await passwordCollection.findOneAndUpdate(
        { id },
        { $set: input },
        { returnDocument: "after" },
      );
      return {
        data: updated,
        status: true,
        statusCode: 200,
        message: "updated successfully!",
      };
    } catch (e) {
      const { message } = e;
      return { message, status: false, statusCode: 500 };
    }
  }

  async get(body) {
    const checkMissing = await this.#validator.checkMissing(
      Object.keys(body),
      this.#notesObjKeys,
      Object.values(body),
    );
    if (checkMissing) {
      const NotesCollection = await this.notes();
      const note = await NotesCollection.findOne(body);

      return !note ? { status: true, note } : { status: false, note: {} };
    } else {
      return { status: false, note: {} };
    }
  }
}

module.exports = notesService;
