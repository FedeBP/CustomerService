# Kubernetes Deployment Configuration

This directory contains Kubernetes manifests for deploying the Customer Management Microservice.

## Components

- **Deployment**: Defines the container configuration, scaling, and update strategy
- **Service**: Exposes the application within the cluster
- **ConfigMap**: Contains non-sensitive configuration
- **Secret**: Contains sensitive configuration (passwords, keys)
- **Ingress**: Exposes the service to external traffic

## Deployment Instructions

### Prerequisites
- Kubernetes cluster
- kubectl configured for your cluster
- Container registry access

### Steps

1. **Update image reference**

   Edit the `deployment.yaml` file to update the image reference with your container registry:
   ```bash
   export DOCKER_REGISTRY=your-registry.example.com
   envsubst < deployment.yaml > deployment-with-registry.yaml
   ```
2. **Create the resources**
    ```bash
    kubectl apply -f configmap.yaml
    kubectl apply -f secret.yaml
    kubectl apply -f deployment-with-registry.yaml
    kubectl apply -f service.yaml
    kubectl apply -f ingress.yaml
   ```
3. **Verify Deployment**
    ```bash
    kubectl get pods -l app=customer-service
    kubectl get svc customer-service
    kubectl get ingress customer-service-ingress
    ```

## Scaling

To scale the deployment:
```bash
kubectl scale deployment customer-service --replicas=4
```

### Infrastructure Dependencies
   This service requires:

- PostgreSQL database
- RabbitMQ

**You should deploy these services separately or use managed cloud services.**