# JavaRank
> Recommendation engine in Java. Based on an ALS algorithm (Apache Spark). Including an automatisation of training a new model after N seconds. And an easier interface.

## Installation

Prerelease stage, you have to get your own build to use the dependency.

###  Requirements
- maven

Check out the project with your IDE.
Most ide will automatically download your maven dependencies.
After this, the project will be ready to be released/modified.


## Usage example
The following example shows, how to init the ch.javarank.service. After training the model will give prediction, which rating the user will likely give for the product.
```java
RecommendationService recommendationService = new RecommendationService(() -> dataProvider(), timeBetweenNewModels, initialDelay);
```
dataProvider() is a Methode, which returns a Collection<InputRating>.
timeBetweenNewModels is the time in seconds between the renewal of the model
initialDelay is the time in seconds for the first delay
  
As soon as the model is ready (see recommendationService.isModelReady()) you can get the prediction like this
```java
Double prediction = recommendationService.getPrediction(2, 3);
```

## Release History

* Prerelease
    * Working prototype

## Meta

SebastianMue

Distributed under the MIT license. See ``LICENSE`` for more information.

[https://github.com/SebastianMue]

## Contributing

1. Fork it (<https://github.com/yourname/yourproject/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request
