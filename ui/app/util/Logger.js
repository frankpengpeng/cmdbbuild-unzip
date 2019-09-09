Ext.define("CMDBuildUI.util.Logger", {
    singleton: true,

    levels: {
        error: 'error',
        debug: 'debug',
        warn: 'warn',
        info: 'info',
        log: 'log'
    },

    /**
     * Log into console
     * @param {String} message The message to log (required).
     * @param {String} level One of: "error", "warn", "info" or "log" (the default is "log").
     * @param {Numeric} code The known code of the message.
     * @param {Object} dump An object to dump to the log as part of the message.
     */
    log: function (message, level, code, dump) {
        var log = {};

        // set level
        log.level = level;
        if (!level) {
            log.level = this.levels.info;
        }

        log.msg = '';
        if (code) {
            log.msg += "Err.code: " + code + " - ";
        }
        log.msg += message;

        // dump object
        if (dump) {
            log.dump = dump;
        }

        Ext.log(log);
    }
});