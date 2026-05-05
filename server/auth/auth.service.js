const databaseService = require("../core/database.service");
const validatorService = require("../core/datavalidate.service");
const config = require("../config/config.json");

class authService {
  #db;
  #client;
  #serviceName;
  #validator;
  #authObjKeys;

  constructor() {
    this.#db = new databaseService();
    this.#validator = new validatorService();
    this.#client = this.#db.client;
    this.#serviceName = "auth";
    this.#authObjKeys = ["username"];
  }

  #database = async () => {
    return this.#db.getMongoDb(config.mongoDbName);
  };

  #collectionJoin = async (name) => {
    return (await this.#database()).collection(name);
  };

  auth = async () => {
    return await this.#collectionJoin(this.#serviceName);
  };

  async #validateAuthObj(obj) {
    const checkMissing = await this.#validator.checkMissing(
      Object.keys(obj),
      this.#authObjKeys,
      Object.values(obj),
    );
    if (!checkMissing) return checkMissing;
    return this.#validator.checkStrings(obj);
  }

  async save(data) {
    try {
      const validateAuth = await this.#validateAuthObj(data);
      if (!validateAuth) {
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

      const authCollection = await this.auth();
      await authCollection.insertOne(data);
      return {
        status: true,
        statusCode: 200,
        message: "auth save successfully!",
      };
    } catch (e) {
      const { message } = e;
      return { message, status: false, statusCode: 500 };
    }
  }

  async get(body) {
    const checkMissing = await this.#validator.checkMissing(
      Object.keys(body),
      this.#authObjKeys,
      Object.values(body),
    );
    if (!checkMissing) {
      return {
        status: false,
        statusCode: 400,
        message: "bad request",
      };
    }

    if (checkMissing) {
      const authCollection = await this.auth();

      const auth = await authCollection.findOne(body);

      return !auth
        ? { status: false, auth: {}, statusCode: 404 }
        : { status: true, statusCode: 200, auth };
    } else {
      return { status: false, auth: {}, statusCode: 500 };
    }
  }
}

module.exports = authService;
