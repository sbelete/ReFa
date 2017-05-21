library(caret)
library(RSQLite)
library(DBI)
sqlite    <- dbDriver("SQLite")
exampledb <- dbConnect(sqlite,wd)
dbListTables(exampledb)
dbGetQuery(exampledb, "SELECT * FROM main")
results <- dbSendQuery(exampledb, "SELECT * FROM main")
data = fetch(results,-1)
data = data[ ,c(2:8,13:15)]

data$real = as.factor(data$real)
data$real = make.names(data$real)
data$c1l1 = as.factor(data$c1l1)
data$wordpress = as.factor(data$wordpress)

control <- trainControl(method="cv", number=10, classProbs=TRUE, summaryFunction=twoClassSummary)
model = train(real ~ ., data=data, method = 'rf', metric="ROC", trControl=control,na.action = na.pass)
saveRDS(model, "model.rds")
model

