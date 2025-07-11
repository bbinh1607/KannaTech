from backend.entity import DeviceEntity
from backend.extensions import db
from backend.utils.handle.hande_exception import handle_exceptions_repository_class

@handle_exceptions_repository_class
class DeviceRepository:
    def __init__(self):
        self.db = db.session

    def create_device(self, device: DeviceEntity) -> DeviceEntity:
        self.db.add(device)
        self.db.commit()
        return device
    
    def get_device_by_id(self, device_id: str):
        device_data = self.db.query(DeviceEntity).filter(DeviceEntity.id == device_id).first()
        return device_data
        
    def get_all_devices(self):
        try:
            device_data = self.db.query(DeviceEntity).all()
            return device_data
        except Exception as e:
            self.db.rollback()
            raise e
        
    def update_device(self, device_id: str, device: DeviceEntity):
        query = self.db.query(DeviceEntity).filter(DeviceEntity.id == device_id)
        query.update(device)
        self.db.commit()
        entity = query.first()
        self.db.refresh(entity)
        return entity


    def delete_device(self, device_id: str):    
        device_data = self.db.query(DeviceEntity).filter(DeviceEntity.id == device_id).first()
        self.db.delete(device_data)
        self.db.commit()
        return device_data

        