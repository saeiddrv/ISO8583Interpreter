# An Interpreter for ISO-8583
A Java library for generating and parsing ISO-8583 financial messages. It uses the builder pattern and provides complete flexibility to provide custom decoders and encoders.

## Usage
To use the library, simply create an ISO8583 object and define the fields you need. You can then set the values for each field and build the ISO-8583 message object.

Here's an example:

```
ISO8583 builder = ISO8583.create()
    .setCharset(StandardCharsets.ISO_8859_1)
    .setMessageLengthInterpreter(2, new HexMessageLengthInterpreter())
    .setHeader(TPDU.fromDecimal("60", "121", "121"), new TPDUHeaderInterpreter())
    .setMTI("0200", new BCDMTIInterpreter());

builder.defineField(2,
    BCD.create(LengthType.LL, 19, ContentPad.RIGHT_0)
        .setDescription("Primary Account Number")
        .setValueFormatter(new MaskCardNumber()));

// set values
builder.buildMessage().setValue(2, "6219861026599414");

// ...
```

## Defining Fields
To define a field, use the defineField() method on your ISO8583 object. You'll need to provide the field number, data type, length, and any other necessary parameters.

For example:

```
builder.defineField(2,
    BCD.create(LengthType.LL, 19, ContentPad.RIGHT_0)
        .setDescription("Primary Account Number")
        .setValueFormatter(new MaskCardNumber()));
```

## Setting Values
Once you've defined your fields, you can set their values using the setValue() method on your ISO-8583 message object.

For example:

```
Message message = builder.buildMessage();
message.setValue(2, "6219861026599414");

byte[] pack = message.pack();
```

## Contributions
This project may have limitations and issues, as it has not been actively maintained and enhanced after being used as a basis for an enterprise project.

Nevertheless, Contributions are welcome! You can fork the repository and submit a pull request with your changes or open an issue to **report errors**.

## License
This project is licensed under the MIT License.

