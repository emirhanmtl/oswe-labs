FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY InvoiceWise.csproj .
RUN dotnet restore InvoiceWise.csproj
COPY . .
RUN dotnet publish InvoiceWise.csproj -c Release -o /app/publish

FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS final
WORKDIR /app
COPY --from=build /app/publish .

# Local cache for the admin invoice-draft backup/restore tool.
RUN mkdir -p /app/drafts && chmod -R 777 /app/drafts

# Views are shipped as loose .cshtml files (see the csproj) so
# AddRazorRuntimeCompilation() can recompile one on the fly after it's edited on disk -
# make sure the runtime user can actually write back to them.
RUN chmod -R 777 /app/Views

RUN echo "OSWE_LAB10{sqli_massassign_binaryformatter_rce_$(date +%s)}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 80
ENV ASPNETCORE_URLS=http://+:80
ENTRYPOINT ["dotnet", "InvoiceWise.dll"]
