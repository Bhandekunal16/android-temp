const databaseService = require("../core/database.service");
const validatorService = require("../core/datavalidate.service");

class passwordManager {
  #db;
  #client;
  #serviceName;
  #validator;
  #passwordObjKeys;
  #getPasswordKeys;

  constructor() {
    this.#db = new databaseService();
    this.#validator = new validatorService();
    this.#client = this.#db.client;
    this.#passwordObjKeys = ["id", "app", "username", "password"];
    this.#getPasswordKeys = ["app", "username", "password"];
    this.#serviceName = "passwords";
  }

  #database = async () => {
    return this.#db.getMongoDb(process.env.MONGO_DB_NAME);
  };

  #collectionJoin = async (name) => {
    return (await this.#database()).collection(name);
  };

  passwords = async () => {
    return await this.#collectionJoin(this.#serviceName);
  };

  async #validatePasswordObj(obj) {
    let { id, ...strings } = obj;
    const checkMissing = await this.#validator.checkMissing(
      Object.keys(obj),
      this.#passwordObjKeys,
      Object.values(obj),
    );
    if (!checkMissing) return checkMissing;
    return this.#validator.checkStrings(strings);
  }

  async save(data) {
    try {
      const validatePassword = await this.#validatePasswordObj(data);
      if (!validatePassword) {
        return {
          status: false,
          statusCode: 400,
          message: "bad request, please check request data!",
        };
      }

      const presentInDbCheck = await this.get(data);
      if (presentInDbCheck.status) {
        return {
          status: false,
          statusCode: 409,
          message: "already present in database",
        };
      }

      const passwordCollection = await this.passwords();
      await passwordCollection.insertOne(data);
      return {
        status: true,
        statusCode: 200,
        message: "password save successfully!",
      };
    } catch (e) {
      const { message } = e;
      return { message, status: false, statusCode: 500 };
    }
  }

  async getAll() {
    try {
      const passwordCollection = await this.passwords();
      const data = await passwordCollection.find().sort({ _id: -1 }).toArray();
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
      const passwordCollection = await this.passwords();
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

  async delete(body) {
    try {
      const passwordCollection = await this.passwords();
      const deleteCheck = await passwordCollection.deleteOne(body);
      return deleteCheck.acknowledged
        ? {
            status: true,
            message: "password deletion successful",
            statusCode: 200,
          }
        : {
            status: false,
            message: "password deletion failed",
            statusCode: 404,
          };
    } catch (e) {
      const { message } = e;
      return { status: false, message, statusCode: 500 };
    }
  }

  async get(body) {
    const checkMissing = await this.#validator.checkMissing(
      Object.keys(body),
      this.#getPasswordKeys,
      Object.values(body),
    );
    if (checkMissing) {
      const passwordCollection = await this.passwords();
      const password = await passwordCollection.findOne(body);

      return !password
        ? { status: true, password, statusCode: 200 }
        : { status: false, password: {}, statusCode: 404 };
    } else {
      return { status: false, password: {}, statusCode: 500 };
    }
  }

  async getById(body) {
    const passwordCollection = await this.passwords();
    const password = await passwordCollection
      .find({ id: body })
      .sort({ _id: -1 })
      .toArray();

    return password
      ? { status: true, password, statusCode: 200 }
      : { status: false, password: {}, statusCode: 404 };
  }
}

module.exports = passwordManager;
