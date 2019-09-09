Ext.define('Override.form.field.VTypes', {
    override: 'Ext.form.field.VTypes',
    /**
     * @cfg {RegExp} usernameValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    usernameValidationRe: /^([\w0-9._-]+$)|((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-||_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+([a-z]+|\d|-|\.{0,1}|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])?([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/,

    /**
     * @cfg {String} usernameValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    usernameValidationText: 'Must cantain an email address or alphanumeric char with ._- \n(valid ex. "jhon.doe" or "jhon_doe@acme-industries.com',

    /**
     * The function used to validate username
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    usernameValidation: function (value, field) {
        return this.usernameValidationRe.test(value);
    },

    /**
     * The function used to check if password value match to passwordConfirmation
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    passwordMatch: function (value, field) {
        var up = field.up().getXType();
        var password = '';
        if (field.up(up)) {
            password = field.up(up).down('[reference=password]');
        }

        return (value == password.getValue());
    },
    /**
     * @cfg {String} passwordMatchText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    passwordMatchText: 'Passwords doesn\'t match',
    /**
     * The function used to validate password values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    passwordValidation: function (value, field) {
        //return this.passwordValidationRe.test(value);

        // TODO: validate password from config
        //     name: "org.cmdbuild.password.differ-from-previous",
        //     name: "org.cmdbuild.password.differ-from-username",
        //     name: "org.cmdbuild.password.enable-password-change-management",
        //     name: "org.cmdbuild.password.forewarning-days",
        //     name: "org.cmdbuild.password.max-password-age-days",
        //     name: "org.cmdbuild.password.min-length",
        //     name: "org.cmdbuild.password.require-digit",    
        //     name: "org.cmdbuild.password.require-lowercase",
        //     name: "org.cmdbuild.password.require-uppercase",

        return value ? true : false;
    },
    /**
     * @cfg {RegExp} passwordValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    passwordValidationRe: /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})/,
    /**
     * @cfg {String} passwordValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    passwordValidationText: 'Must cantain a-z A-Z 0-9 and must be at least 8 characters long',
    /**
     * @cfg {RegExp} IPv4AddressRe
     * RegExp for the value to be tested against within the validation function.
     */
    IPv4AddressRe: /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/,
    /**
     * @cfg {String} IPv4AddressText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    IPv4AddressText: 'Must be a numeric IP address',
    /**
     * @cfg {RegExp} IPv4AddressMask
     * vtype Mask property: The keystroke filter mask.
     */
    IPv4AddressMask: /[\d\.]/i,
    /**
     * The function used to validate IP v4 values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    IPv4Address: function (value) {
        return this.IPv4AddressRe.test(value);
    },

    /**
     * @cfg {RegExp} IPv6AddressRe
     * RegExp for the value to be tested against within the validation function.
     */
    IPv6AddressRe: /^s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:)))(%.+)?s*(\/([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8]))?$/,
    /**
     * @cfg {String} IPv6AddressText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    IPv6AddressText: 'Must be a valid IP v6 address',
    /**
     * @cfg {RegExp} IPv6AddressMask
     * vtype Mask property: The keystroke filter mask.
     */
    IPv6AddressMask: /[0-9a-fA-F:\/]/i,
    /**
     * The function used to validate IP v6 values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    IPv6Address: function (value) {
        return this.IPv6AddressRe.test(value);
    },

    /**
     * @cfg {RegExp} IPAddressRe
     * RegExp for the value to be tested against within the validation function.
     */
    IPAddressRe: /(^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$)|(^s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:)))(%.+)?s*(\/([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8]))?$)/,
    /**
     * @cfg {String} IPAddressText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    IPAddressText: 'Must be a valid IP address',
    /**
     * @cfg {RegExp} IPAddressMask
     * vtype Mask property: The keystroke filter mask.
     */
    IPAddressMask: /([\d\.])|([0-9a-fA-F:\/])/i,
    /**
     * The function used to validate IP values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    IPAddress: function (value) {
        return this.IPAddressRe.test(value);
    },

    /**
     * @cfg {RegExp} IPAddressRe
     * RegExp for the value to be tested against within the validation function.
     */
    timeRe: /^(2[0-3]|[01]?[0-9]):([0-5]?[0-9])(:([0-5]?[0-9]))?$/,
    /**
     * @cfg {String} IPAddressText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    timeText: 'Must be a valid time 24-hour clock',
    /**
     * @cfg {RegExp} IPAddressMask
     * vtype Mask property: The keystroke filter mask.
     */
    timeMask: /([\d{2}\:])/,
    /**
     * The function used to validate time values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    time: function (value) {
        return this.timeRe.test(value);
    },

    /**
     * @cfg {RegExp} nameInputValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    nameInputValidationRe: /^[a-zA-Z]+[a-zA-Z0-9_-]+$/,

    /**
     * @cfg {String} nameInputValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    nameInputValidationText: 'This field can\'t start with "_" or number. Allowed char are Alphanumeric, dash and underscore.',

    /**
     * The function used to validate name input
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    nameInputValidation: function (value, field) {
    
        return this.nameInputValidationRe.test(value);
    }



});